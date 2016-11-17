# C2WT-JAVA refactoring




## FederationManager

### Hosting

  * FederationManager is now hosted through
    - Console App (see `FederationManagerHost`)
    - Dropwizard (see `fedmanager-host` project)
  * See `fedmanager-exec/pom.xml`'s profile section


### Configuration

  * New configuration file for `dropwizard`-style hosting (`yml`):
    - filePath to config file added as argument (see `exec/pom.xml`)
    - parameter names changed to `FederationManagerParameter` fields' names
  
  * _NEW_ `RootPathEnvVarKey` - the environment variable that indicates root path of the running project
  (previously `C2WTROOT`)
  * _OBSOLETE_ `LockFile` - DROPPED. Using `System.getEnv(${RootPathEnvVarKey})/__lock__` (hardcoded) instead.
  * _OBSOLETE_ `DBName` - DROPPED. Not using remote DB to log (seriously).


## New inheritances

  * `COARandomDuration` extends `COADuration` instead of `COANode`




---

## Red flags

- lack of OOP (static state hell)
- empty catch blocks
- while(variable) { try{ Thread.sleep(500); } catch (Exception e) {} };
- throw new Exception("message")
- Thread.sleep() in static constructor
- public static final String MODE_X = "X"; instead of enums
- logLevel passed as string
- custom logger class that logs to a remote DB and creates a Thread on every log call
- no coding standards. field names: federation_name, FOM_file_name, _federationEndTime, ...
- state machine with multiple boolean flags instead of states (enum) and transitions
- runtime monkey patching: TimeAdvanceRequestHandler
- throw new RuntimeException("Invalid parameters for arrived interaction"); -- IllegalArgumentException / NullPointerException