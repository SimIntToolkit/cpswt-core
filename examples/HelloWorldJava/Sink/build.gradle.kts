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
val rtiHome: String? = System.getenv("RTI_HOME")

val archivaHostId: String by project
val archivaPort: String by project

val version: String by project

dependencies {
    implementation(group="org.apache.logging.log4j", name="log4j-core", version="2.17.1")

    implementation(files("$rtiHome/lib/portico.jar"))

    implementation(group="edu.vanderbilt.vuisis.cpswt", name="utils", version=version)

    implementation(group="edu.vanderbilt.vuisis.cpswt", name="root", version=version)

    implementation(group="edu.vanderbilt.vuisis.cpswt", name="base-events", version=version)

    implementation(group="edu.vanderbilt.vuisis.cpswt", name="federate-base", version=version)

    implementation(group="edu.vanderbilt.vuisis.cpswt", name="config", version=version)
}

application {
    mainClass.set("edu.vanderbilt.vuisis.cpswt.hla.helloworldjava.sink.Sink")
    applicationDefaultJvmArgs = listOf("-Djava.net.preferIPv4Stack=true")
}

tasks.named<JavaExec>("run") {
    args = listOf("-configFile", "conf/Sink.json")
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

    processBuilder.directory(projectDir)

    val environment = processBuilder.environment()
    environment["CLASSPATH"] = classPath
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

tasks.register("runAsynchronous") {
    dependsOn("build")
    doLast {
        spawnProcess()
    }
}

val runAsynchronousBatch = tasks.register("runAsynchronousBatch") {
    dependsOn("build")
    doLast {
        spawnProcessBatch()
    }
}

tasks.register("waitForFederate") {
    doLast {
        spawnedProcess.waitFor()
    }
}

tasks.register("killFederate") {
    doLast {
        spawnedProcess.destroy()
    }
}
