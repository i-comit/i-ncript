# <p align="center">i-ncript</p>

i-ncript is a USB based data encryption app built with Java. **No installation wizard** required, just drop the app in your USB drive and run!

Unlike other file managers like 7-zip which uses their own file manager GUI, i-ncript uses the native operating system file manager rather than create another window, making it similar to BitLocker without the restriction of working only on Windows.

It has a very **simple user interface**: A login page and a minimal dashboard. But under the hood it is capable of much more.

Implementing **multithreading**, it can encrypt and decrypt hundreds of files in under a few seconds using **AES**, the highest standard for encryption algorithms.

It has a unique encryption function called **Hot Filer**, which can keep watch of the encryption folder and detect any new unencrypted files that has been added and immediately encrypts them, **all within your native operating system file manager!**


# <p align="center">builds</p>
There are 2 packages that you can compile from, **com.i_comit.windows** and **com.i_comit.windows.dev**.

com.i_comit.windows is the main version which has the GUI implementation, while com.i_comit.windows.dev is command based and uses a different encryption strategy of zipping up the encrypted folder and encrypting that, rather than individual files within that folder.

Main.java is the Main class for com.i_comit.windows and Authenticator.java is the main class for com.i_comit.windows.dev

# <p align="center">contact</p>
Currently developed by Khiem Luong <khiemluong@i-comit.com>
