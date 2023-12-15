/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

import java.io.PrintWriter
import java.nio.file.Files


plugins {
    java
    application
}

// THE EXPLICIT TYPE OF NULLABLE-STRING (String?) IS NEEDED HERE BECAUSE KOTLIN-SCRIPT CANNOT
// DETERMINE THE RETURN TYPE OF "System.getenv()" (WHICH IS INDEED String?) THROUGH TYPE-INFERENCE
val rtiHome = System.getenv("RTI_HOME")

val version: String by project

dependencies {
    implementation(group="org.apache.logging.log4j", name="log4j-core", version="2.17.1")

    implementation(files("$rtiHome/lib/portico.jar"))

    implementation(group="edu.vanderbilt.vuisis.cpswt", name="federation-manager", version=version)
}

application {
    mainClass.set("edu.vanderbilt.vuisis.cpswt.hla.FederationManager")
    applicationDefaultJvmArgs = listOf("-Djava.net.preferIPv4Stack=true")
}

tasks.named<JavaExec>("run") {
    args = listOf("-configFile", "federationManagerConfig.json")
}

fun getCommandList(): List<String> {
    val runTask = tasks.named<JavaExec>("run").get()

    val mainClass: String = runTask.mainClass.get()

    // EXPLICIT TYPE OF List<String> IS NEEDED HERE, AS IS THE NON-NULL ASSERTION (!!) (DESPITE WHAT INTELLISENSE SAYS)
    // OTHERWISE THE INFERRED TYPE OF commandList (BELOW) IS List<Any> INSTEAD OF List<String>
    val jvmArgs: List<String> = runTask.jvmArgs!!
    val argList: List<String> = runTask.args!!

    val commandList: List<String> = listOf("java") + jvmArgs + listOf(mainClass) + argList

    return commandList
}

fun getXTermCommandList(): List<String> {
    val commandList = getCommandList()

    val xtermCommandList = listOf(
            "xterm", "-geometry", "220x80", "-fg", "black", "-bg", "white", "-e", commandList.joinToString(" ")
    )

    return xtermCommandList
}

lateinit var spawnedProcess: Process

fun configureProcessBuilder(processBuilder: ProcessBuilder) {
    val runTask = tasks.named<JavaExec>("run").get()
    val classPath = runTask.classpath.asPath

    val environment = processBuilder.environment()
    environment.put("CLASSPATH", classPath)
}

fun spawnProcess() {
    val xtermCommandList = getXTermCommandList()

    val processBuilder = ProcessBuilder(xtermCommandList)
    configureProcessBuilder(processBuilder)

    spawnedProcess = processBuilder.start()
}

fun spawnProcessBatch() {
    val commandList = getCommandList()

    val processBuilder = ProcessBuilder(commandList)
    configureProcessBuilder(processBuilder)

    val statusDirectory = File(projectDir, "StatusDirectory")
    if (!statusDirectory.exists()) {
        Files.createDirectory(statusDirectory.toPath())
    }
    val stdoutFile = File(statusDirectory, "stdout")
    val stderrFile = File(statusDirectory, "stderr")

    processBuilder.redirectOutput(stdoutFile)
    processBuilder.redirectError(stderrFile)
    spawnedProcess = processBuilder.start()

    val pid = spawnedProcess.pid()

    PrintWriter(File(statusDirectory, "pid")).use {
        it.println(pid)
    }
}

val runFederationManagerAsynchronous = tasks.register("runFederationManagerAsynchronous") {
    doLast {
        spawnProcess()
        Thread.sleep(10000)
    }
}

val runFederationManagerAsynchronousBatch = tasks.register("runFederationManagerAsynchronousBatch") {
    doLast {
        spawnProcessBatch()
        Thread.sleep(15000)
    }
}

val runFederatesAsynchronous = tasks.register("runFederatesAsynchronous") {
    mustRunAfter(runFederationManagerAsynchronous)
    dependsOn(runFederationManagerAsynchronous)
    dependsOn(":PingCounter:runAsynchronous")
    dependsOn(":Sink:runAsynchronous")
    dependsOn(":Source:runAsynchronous")
}

val runFederatesAsynchronousBatch = tasks.register("runFederatesAsynchronousBatch") {
    mustRunAfter(runFederationManagerAsynchronousBatch)
    dependsOn(runFederationManagerAsynchronousBatch)
    dependsOn(":PingCounter:runAsynchronousBatch")
    dependsOn(":Sink:runAsynchronousBatch")
    dependsOn(":Source:runAsynchronousBatch")
}

val runFederatesInteractive = tasks.register("runFederatesInteractive") {
    mustRunAfter(runFederatesAsynchronous)
    dependsOn(runFederatesAsynchronous)

    doLast {
        print("Press ENTER to terminate: ")
        System.out.flush()
        System.`in`.read()

        spawnedProcess.destroy()
    }
}

tasks.register("killFederates") {
    dependsOn(":PingCounter:killFederate")
    dependsOn(":Sink:killFederate")
    dependsOn(":Source:killFederate")
}

tasks.register("runFederation") {
    mustRunAfter(runFederatesInteractive)
    dependsOn(runFederatesInteractive)
    finalizedBy("killFederates")
}

tasks.register("waitForFederates") {
    dependsOn(":PingCounter:waitForFederate")
    dependsOn(":Sink:waitForFederate")
    dependsOn(":Source:waitForFederate")
    doLast {
        spawnedProcess.waitFor()
    }
}

val runFederationBatch = tasks.register("runFederationBatch") {
    mustRunAfter(runFederatesAsynchronousBatch)
    dependsOn(runFederatesAsynchronousBatch)
    finalizedBy("waitForFederates")
}

tasks.named("clean") {
    dependsOn(":PingCounter:clean")
    dependsOn(":Sink:clean")
    dependsOn(":Source:clean")
}
