# datadeni

This app works by checking the serialnumber of a USB drive, hashing it and storing it in a key, then takes the user input password and hashing that, also storing it in a key.

When the user wants to access the files, the app checks off the hash of the USB drive serial number and the password.

Current issue: Cannot zip folder in order to hash it if it has subfolders within it. Must find way to encrypt folders recursively or change subfolder permissions.
