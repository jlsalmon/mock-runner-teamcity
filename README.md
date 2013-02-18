mock-runner-teamcity
====================

TeamCity plugin to build RPMs for multiple architectures using mock

Usage
-----

* Run `ant dist` in the root directory
* Copy `dist/mock-runner.zip` to `<teamcity user home directory>/.BuildAgent/plugins`
* Restart TeamCity server (`<teamcity installation directory>/bin/runAll.sh [start|stop]`)

Compatibility
-------------

Compatible with TeamCity 7.x. Not tested on earlier versions.
