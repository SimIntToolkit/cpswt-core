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

plugins {
    java
    application
}

val rtiHome = System.getenv("RTI_HOME")
val version: String by project

dependencies {
    implementation(group="org.apache.logging.log4j", name="log4j-core", version="2.14.1")

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

var spawnedProcess: Process? = null

fun spawnProcess() {
    val runTask = tasks.named<JavaExec>("run").get()

    val mainClass: String = runTask.mainClass.get()
    val jvmArgs: List<String>? = runTask.jvmArgs
    val argList: List<String>? = runTask.args
    val classPath = runTask.classpath.asPath

    val commandList: List<String> = listOf("java") + (jvmArgs ?: listOf()) + listOf(mainClass) + (argList ?: listOf())

    val xtermCommandList = listOf(
        "xterm", "-geometry", "220x80", "-fg", "black", "-bg", "white", "-e", commandList.joinToString(" ")
    )

    val processBuilder = ProcessBuilder(xtermCommandList)
    val environment = processBuilder.environment()
    environment.put("CLASSPATH", classPath)

    spawnedProcess = processBuilder.start()
}

val runAsynchronous = tasks.register("runAsynchronous") {
    doLast {
        spawnProcess()
        Thread.sleep(10000)
    }
}

val runFederates = tasks.register("runFederates") {
    mustRunAfter(runAsynchronous)
    dependsOn(":PingCounter:runAsynchronous")
    dependsOn(":Source:runAsynchronous")
    dependsOn(":Sink:runAsynchronous")

    doLast {
        print("Press ENTER to terminate: ")
        System.out.flush()
        System.`in`.read()

        spawnedProcess?.destroy()
    }
}

tasks.register("killFederates") {
    mustRunAfter(runFederates)
    dependsOn(":PingCounter:killFederate")
    dependsOn(":Source:killFederate")
    dependsOn(":Sink:killFederate")
}

tasks.register("runFederation") {
    dependsOn(":runAsynchronous")
    dependsOn(":runFederates")
    dependsOn(":killFederates")
}