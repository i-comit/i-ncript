# i-ncript

Main class is Authenticator.java
This app works by checking the serialnumber of a USB drive, hashing it and storing it in a key, then takes the user input password and hashing that, also storing it in a key.

When the user wants to access the files, the app checks off the hash of the USB drive serial number and the password.

Currently works on Windows.
