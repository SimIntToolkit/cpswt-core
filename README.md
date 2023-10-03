# Project `"cpswt-core"` for CPSWT

The cpswt-core project contains all software needed to run Java-based federates in CPSWT.

> ##### Table of Contents
> 
> * [Project `"cpswt-core"` for CPSWT](#project-cpswt-core-for-cpswt)
>   * [Constituent Modules](#constituent-modules)
>     * [cpswt-core/root](#cpswt-core-root)
>     * [cpswt-core/base-events](#cpswt-core-base-events)
>     * [cpswt-core/coa](#cpswt-core-coa)
>     * [cpswt-core/config](#cpswt-core-config)
>     * [cpswt-core/federate-base](#cpswt-core-federate-base)
>     * [cpswt-core/federation-manager](#cpswt-core-federation-manager)
>     * [cpswt-core/utils](#cpswt-core-utils)
>   * [CPSWT Software Installation and Instructions to Build and Publish cpswt-core Modules on Ubuntu 20.04 LTS (Focal Fossa)](#cpswt-software-installation-and-instructions-to-build-and-publish-cpswt-core-modules-on-ubuntu-20-04-lts-focal-fossa)
>     * [General Software Installation](#general-software-installation)
>       * [Install Needed Packages](#install-needed-packages)
>       * [Install Portico 2.1.0](#install-portico-2-1-0)
>       * [Install Apache Archiva version 2.2.5](#install-apache-archiva-version-2-2-5)
>     * [Software for Java-based Federates](#software-for-java-based-federates)
>       * [Working with Gradle](#working-with-gradle)
>       * [Build CPSWT Java Packages and Publish to Archiva](#build-cpswt-java-packages-and-publish-to-archiva)

## Constituent Modules

`cpswt-core` contains the core modules for the implementation of the Cyber-Physical Systems Wind Tunnel (CPSWT).
In particular, it contains modules to allows Java-based federates to participate in federations,
the federation manager module which is responsible for coordinating federation execution, and a
module for COA management.

The modules used for CPSWT functionality are as follows. Any modules not listed are under development or are not currently being used.

### cpswt-core/root

This module contains the Java base classes for Java classes that implement HLA interaction and object classes in
CPSWT. These Java base classes, **InteractionRoot** and **ObjectRoot**, actually contain all of the functionality
needed to implement *any* HLA interaction or object class in Java. Importantly, they allow Java-based federates to use
*dynamic messaging*: this enables the federates to manipulate any HLA interaction or object class instance using only
the InteractionRoot or ObjectRoot Java base classes, respectively. That is, an HLA interaction or object instance
can be manipulated without an explicit corresponding Java class.

### cpswt-core/base-events

The modules contains HLA interaction and object classes that are “built-in” to CPSWT.  These classes are derived from InteractionRoot and ObjectRoot.

**Class List (indentation shows inheritance):**

HLA Interactions:

```default
InteractionRoot
  C2WInteractionRoot
    ActionBase
    EmbeddedMessaging
    FederateJoinInteraction
    FederateResignInteraction
    OutcomeBase
    SimLog
      VeryLowPrio
      LowPrio
      MediumPrio
      HighPrio
    SimulationControl
      SimPause
      SimResume
      SimEnd
```

HLA Objects:

```default
ObjectRoot
  FederateObject
```

### cpswt-core/coa

This module contains the Java classes necessary to implement **Courses of Action (COA)**,
i.e. complex behavior that is designed into a federation via its federation model.

### cpswt-core/config

This module contains Java classes that determine the contents of federate configuration files, as well as read them.

### cpswt-core/federate-base

This module contain Java classes that implement behavior that all federates must have. Among them are:

* **SynchronizedFederate** – the base class of all Java federates that are instantiated directly from their representation in a CPSWT model.
* **InteractionMappingBase** and **InteractionMappingManager** – classes that provide base functionality for Mapper federates.

### cpswt-core/federation-manager

This module contains the Java classes that implement the FederationManager, which:

* Must be executed first for any federation
* Coordinates the execution of all federates in the federation so that they all initialize themselves and start
  execution simultaneously.

### cpswt-core/utils

This module contains various utility classes used by the other modules.

<a id="cpswtjavasoftwareinstallation"></a>

## CPSWT Software Installation and Instructions to Build and Publish cpswt-core Modules on Ubuntu 20.04 LTS (Focal Fossa)

> ##### Table of Contents
> 
> * [General Software Installation](#general-software-installation)
>   * [Install Needed Packages](#install-needed-packages)
>   * [Install Portico 2.1.0](#install-portico-2-1-0)
>   * [Install Apache Archiva version 2.2.5](#install-apache-archiva-version-2-2-5)
> * [Software for Java-based Federates](#software-for-java-based-federates)
>   * [Working with Gradle](#working-with-gradle)
>   * [Build CPSWT Java Packages and Publish to Archiva](#build-cpswt-java-packages-and-publish-to-archiva)

The following CPSWT installation instructions were performed on a VirtualBox VM with 128 GB Disk and 12GB RAM.

Currently, only Ubuntu 20.04 LTS (Focal Fossa) is supported.

#### NOTE
There are several text boxes in the instructions below that contain commands that you need to execute at the shell command-line.

To copy any of these commands, hover over the textbox and a small “copy” button will appear at the right of the textbox
(two rounded rectangles, one on top of the other).  Press this button, and the command will be copied to your
clipboard.

Then, to paste the command in your shell, select your shell window and press SHIFT-CTRL-V.

### General Software Installation

This sections contains instructions for the installation of 3rd-party software that is needed to run CPSWT federations.

#### Install Needed Packages

1. After initial install of Ubuntu 20.04 LTS, be sure to upgrade all of the currently installed packages:
   1. Execute the following command to update the available packages for your Ubuntu installation:
      ```bash
      sudo apt update
      ```
   2. Execute the following command to upgrade all of the currently installed packages in your Ubuntu installation:
      ```bash
      sudo apt upgrade -y
      ```

<a id="install-ubuntu-packages"></a>
1. The following packages must be installed:
   * apt-transport-https
   * bison
   * build-essential
   * ca-certificates
   * clang
   * curl
   * doxygen
   * flex
   * gcc
   * gdb
   * git
   * gradle
   * graphviz
   * libboost1.71-all-dev
   * libcppunit-dev
   * libjsoncpp-dev
   * libosgearth-dev
   * libqt5opengl5-dev
   * libwebkit2gtk-4.0-37
   * libxml2-dev
   * lld
   * make
   * mongodb
   * mpi-default-dev
   * openjdk-8-jdk
   * openjdk-17-jdk
   * openscenegraph-plugin-osgearth
   * perl
   * python2
   * python3
   * python3-pip
   * python-is-python2
   * qt5-qmake
   * qtbase5-dev
   * qtbase5-dev-tools
   * qtchooser
   * software-properties-common
   * wget
   * xterm
   * zlib1g-dev

   They can be installed with the following command:
   ```bash
   sudo apt install -y apt-transport-https bison build-essential ca-certificates clang curl doxygen flex gcc gdb git gradle graphviz libboost1.71-all-dev libcppunit-dev libjsoncpp-dev libosgearth-dev libqt5opengl5-dev libwebkit2gtk-4.0-37 libxml2-dev lld make mongodb mpi-default-dev openjdk-8-jdk openjdk-17-jdk openscenegraph-plugin-osgearth perl python2 python3 python3-pip python-is-python2 qt5-qmake qtbase5-dev qtbase5-dev-tools qtchooser software-properties-common wget xterm zlib1g-dev
   ```
2. The following python packages (for python3) must also be installed:
   * jinja2
   * matplotlib
   * numpy
   * pandas
   * posix_ipc
   * python-dateutil
   * scipy
   * seaborn
   * webgme-bindings

   They can be installed with the following command:
   ```bash
   sudo python3 -m pip install --system --upgrade jinja2 matplotlib numpy pandas posix_ipc python-dateutil scipy seaborn webgme-bindings
   ```
3. Set `java-17-openjdk-amd64` to be the default java used by your system by executing the following command:
   ```bash
   sudo update-java-alternatives -s java-1.17.0-openjdk-amd64
   ```

   #### NOTE
   Ignore any errors output by the above command.
4. There will be several modifications to your `.bashrc` file in these instructions.  The first is set your `JAVA_HOME` to the directory of your
   `Java` installation.

   Granted the `openjdk-17-jdk` package was installed in step 1, the following command should be appended to your `.bashrc` file in your home directory:
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   ```

   #### NOTE
   To see a list of all environment variables that need to be defined or modified in your `.bashrc` file,
   see environmentvariablesummary at the end of this document.

   <a id="modify-environment"></a>

   #### NOTE
   Whenever command is added in your `.bashrc` file, it will only have an effect when you spawn and use a new shell, and this effect is needed **immediately**.
   So, whenever a command is added to your `.bashrc` in these instructions either:
   * Kill your current shell, spawn a new one, and use this new shell to continue with these installation instructions.
   * Type the command directly into your current shell, in this case

     *export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64*.

#### Install Portico 2.1.0

1. As these instructions are being written, a compressed tarball to install Portico version 2.1.0 can be downloaded by typing the following
   URL into your Google Chrome web brower.
   ```bash
   https://master.dl.sourceforge.net/project/portico/Portico/portico-2.1.0/portico-2.1.0-linux64.tar.gz?viasf=1
   ```

   However, if this link does not work, you should be able to download the Portico version 2.1.0 as follows:
   1. Go to [https://porticoproject.org](https://porticoproject.org) using your Google Chrome web browser
   2. Click on `portico-2.1.0-linux64.tar.gz` at the right side of the resulting page, under `Linux 64-bit` and `May 6th`
   3. On the resulting page, click on `portico-2.1.0-linux64.tar.gz`
   4. The file `portico-2.1.0-linux64.tar.gz` should be downloaded into your `Downloads` directory under your home directory.
2. Execute the following commands to untar the `portico-2.1.0-linux64.tar.gz` tarball into your home directory:
   ```bash
   cd
   tar xf <YOUR-HOME-DIRECTORY>/Downloads/portico-2.1.0-linux64.tar.gz
   ```
3. Append the following command to your `.bashrc` file:
   ```bash
   export RTI_HOME="<YOUR-HOME-DIRECTORY>/portico-2.1.0"
   ```

   After doing this [remember](#modify-environment) to either kill your current shell and spawn a new one or type this command into your current shell.

   #### NOTE
   To see a list of all environment variables that need to be defined or modified in your `.bashrc` file,
   see environmentvariablesummary

   <a id="archiva-install"></a>

#### Install Apache Archiva version 2.2.5

1. As these instructions are being written, a compressed tarball to install Apache Archiva version 2.2.5 can be downloaded by typing the following
   URL into your Google Chrome web brower.
   ```bash
   https://archive.apache.org/dist/archiva/2.2.5/binaries/apache-archiva-2.2.5-bin.tar.gz
   ```

   However, if this link does not work, you should be able to download the Apache Archiva version 2.2.5 as follows:
   1. Go to [https://archiva.apache.org](https://archiva.apache.org) using your Google Chrome web browser
   2. Click on the `Introduction` menu item at the top of the resulting page
   3. On the resulting dropdown menu, click on `Downloads`
   4. In the `Previous Versions` section of the resulting page, click on `Apache Archiva Archives`
   5. On the resulting page, click on `2.2.5/`
   6. On the resulting page, click on `binaries/`
   7. On the resulting page, click on `apache-archiva-2.2.5-bin.tar.gz`
   8. The file `apache-archiva-2.2.5-bin.tar.gz` should be downloaded into your `Downloads` directory under your home directory.
2. Untar the `apache-archiva-2.2.5-bin.tar.gz` tarball into your `/opt` directory:
   1. Become `root` using the following command:
      ```bash
      sudo su -
      ```
   2. Change your current directory to the `/opt` directory:
      ```bash
      cd /opt
      ```
   3. Untar the `apache-archiva-2.2.5-bin.tar.gz` tarball:
      ```bash
      tar xf <YOUR-HOME-DIRECTORY>/Downloads/apache-archiva-2.2.5-bin.tar.gz
      ```
   4. DON’T EXIT OUT OF THE `root` USER ID YET – GO TO THE NEXT STEP BELOW
3. Start the Archiva server and set up Archiva to execute on boot:
   1. Edit the file `/etc/systemd/system/archiva.service` using your favorite editor *by executing the editor from the command-line, i.e. as root*.
      It should not exist yet, and so should be created when you edit it.
   2. Place the following text in this file:
      ```bash
      [Unit]
      Description=Archiva Server
      Documentation=https://archiva.apache.org/docs/2.2.5/
      After=network.target

      [Service]
      Type=forking
      PIDFile=/opt/apache-archiva-2.2.5/logs/archiva.pid
      WorkingDirectory=/opt/apache-archiva-2.2.5
      ExecStart=bash -ic "export PATH=/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH ; exec /opt/apache-archiva-2.2.5/bin/archiva start"
      ExecStop=/opt/apache-archiva-2.2.5/bin/archiva stop

      [Install]
      WantedBy=multi-user.target
      ```

   > 1. Save the file and exit the editor
   > 2. Type the following two commands at the root shell prompt:
   >    ```bash
   >    systemctl enable archiva
   >    systemctl start archiva
   >    ```
   > 3. Exit out of the `root` userid:
   >    ```bash
   >    exit
   >    ```
   > 4. The Archiva server should now be started and will start automatically on boot.
4. Test and set up the Archiva server:
   1. Using your Google Chrome web browser, go to [http://localhost:8080](http://localhost:8080).  The resulting page should be the Archiva home page for your Archiva server.
      > #### NOTE
      > Archiva can take several seconds to start up.  If your browser reports that it cannot load the page, or the page is taking a long time to load,
      > wait 60 seconds (ample time) and try again.
   2. In the upper right-hand corner of you Archiva server’s home page, click `Create Admin User`
   3. On the resulting page, enter the following information:
      * Username:  `admin`
      * Full Name:  `<whatever you think is appropriate>`
      * Password: `adminpass123`
      * Confirm Password: `adminpass123`
      * Email Address: `<your email address>`
      * Validated: `<check the box>`
      * Locked: `<LEAVE BOX UNCHECKED>`
      * Change password required: `<LEAVE BOX UNCHECKED>`
   4. Press the `Save` button
   5. Archiva is good to go!

#### NOTE
It is important that you use `admin` and `adminpass123` as your Archiva administrator username and password, respectively,
as they are used in other parts of these instructions.

### Software for Java-based Federates

The following instructions show how to install, build and publish software needed to use Java-based federates in CPSWT.

#### Working with Gradle

Gradle is the build tool the CPSWT project uses to build, test, and publish its software to the [installed Archiva repository on your system](#archiva-install).

For Gradle to work properly in this capacity, the following steps must be performed:

1. Create the `.gradle` directory under your home directory, if it doesn’t already exist, by executing the following commands:
   ```bash
   cd
   mkdir .gradle
   ```
2. Edit the <YOUR-HOME-DIRECTORY>/.gradle/gradle.properties file using your favorite editor and give it the contents below:
   ```bash
   archivaUser=admin
   archivaPassword=adminpass123
   archivaHostId=localhost
   archivaPort=8080
   version=0.8.0-SNAPSHOT

   org.gradle.console=plain

   omnetppHome=/opt/omnetpp-5.6.2
   ```

   <a id="gradle-wrapper"></a>
3. IMPORTANT:  to execute gradle to build/test/publish software, we will be using the `gradle wrapper` executable, which will reside in the directories
   where we perform the building/testing/publishing.

   #### NOTE
   DO NOT EXECUTE the following two commands below – they’re only there to show how to set up the `gradle wrapper`

   1. To set up the `gradle wrapper` executable, change to the given directory (this happens in a few places below) and execute the
      following command in your shell:
      ```default
      gradle wrapper --gradle-version=8.0
      ```
   2. To execute the gradle-wrapper, use the following command while in the given directory:
      ```default
      ./gradlew <options> [arguments]
      ```

#### Build CPSWT Java Packages and Publish to Archiva

<a id="cpswt-home"></a>

#### NOTE
To build CPSWT Packages and publish them to Archiva, we first need to clone the git repositories where they reside.
To do this, it is best to create a directory under your home directory where all needed CPSWT git repositories will be cloned (there 3 total
needed for these instructions).

It is recommended that you call this directory `cpswt`.  To create it, enter the following commands:

```default
cd
mkdir cpswt
```

This directory will be referred to as `<CPSWT-HOME>` for the remainder of these instructions.

**IMPORTANT**:  `<CPSWT-HOME>` is an *absolute path* , not a relative path.  For instance, if
you made `<CPSWT-HOME>` the cpswt directory under `<YOUR-HOME-DIRECTORY>`, then `<CPSWT-HOME>`
represents “`<YOUR-HOME-DIRECTORY>/cpswt`”.

1. Clone the cpswt-core git repository:
   1. Change your directory to <[CPSWT-HOME](#cpswt-home)>:
      ```bash
      cd <CPSWT-HOME>
      ```
   2. Clone the repository:
      ```bash
      git clone git@github.com:SimIntToolkit/cpswt-core.git
      ```
2. Change to the `<CPSWT-HOME>/cpswt-core/cpswt-core` directory (*you read that right – cpswt-core appears twice*):
   ```bash
   cd <CPSWT-HOME>/cpswt-core/cpswt-core
   ```
3. Install the [gradle wrapper](#gradle-wrapper):
   ```bash
   gradle wrapper --gradle-version=8.0
   ```
4. Execute the following sequence of commands **in order**:
   ```bash
   ./gradlew :utils:publish
   ./gradlew :root:publish
   ./gradlew :base-events:publish
   ./gradlew :coa:publish
   ./gradlew :config:publish
   ./gradlew :federate-base:publish
   ./gradlew :federation-manager:publish
   ./gradlew :fedmanager-host:publish
   ```
5. To test if the Java packages have been published to Archiva, do the following:
   1. Go to `http://localhost:8080` using your Google Chrome browser
   2. On the resulting page, click on `Browse` in the upper left-hand corner under `Artifacts`
   3. Under `Browse Repository` in the top-middle-left of the page, click on the small down-arrow on the right side of a small textbox.
   4. In the resulting drop-down menu, click `Archive Managed Snapshot Repository`
   5. Click on `org.cpswt`, which should be a short ways down the page from `Browse Repository` mentioned above.
   6. A list should appear that contains all of the packages you published above using `./gradlew` (in alphabetical order):

      <a id="published-java"></a>
      * base-events
      * coa
      * config
      * federate-base
      * federation-manager
      * fedmanager-host
      * root
      * utils

**This concludes the setup for CPSWT for the HelloWorldJava example!**
