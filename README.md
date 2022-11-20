# <p align="center">i-ncript™</p>
![image](https://i-comit.com/wp-content/uploads/2022/11/Screenshot_20221118_052656.png)

### i-ncript is an offline & portable (no installation) USB based data encryption application
Current Version: 1.7.2

Copyright 2022 [i-comit LLC](https://i-comit.com/). All rights reserved.

## __I-NCRIPT STANDARD OPERATING PROCEDURE 11/19/2022__

i-ncript operates with 3 tool panels connected to their respective folders: STORE, N-BOX, and O-BOX, which you can cycle through via the button on the bottom left. Within these panels are primary tools which are accessible in all 3 panels, and some others which are exclusive to its respective panel.
It also has a tabbed pane to the right of its interface, containing panels which contains a drop box, output log, and supplementary information about the APP, including this SOP.
Then on the left, there is a file tree view which recursively lists out all the files in within its respective folder, where you can retrieve the file size and creation date, open it by double clicking, and drag it to the drop box for encrypt/decrypt.
Finally, i-ncript has a set of hotkeys which you can use to activate functions without needing a mouse.

_If this is your first time using i-ncript, we recommend that you read [FIRST TIME SETUP]._

## [FIRST TIME SETUP]

No installation required, just extracts the contents of the zip file into the root of your USB drive.
If you've received a USB with the contents in i-ncript-[version].zip already extracted, then please skip step 1.

1. Extract the contents of i-ncript-[version].zip into the root of your USB flash drive (you can delete the zip file after extraction is done)
2. Go into the -------- folder and click on i-ncript.exe
3. Accept the EULA and Terms of Use (pdf copies can be found in app folder)
4. 3 folders should then be made within the -------- folder
5. Create a username and password, this will be stored as a 256bit key
6. You now have access to i-ncript's tools, read more to see how to use them below:

## [PRIMARY TOOLS]

- ENCRYPT & DECRYPT \
These two radio buttons are the main tools of i-ncript, and pressing either buttons will run the respective encryption task, using the AES cipher. Encrypted files will have an .enc file extension.
- STOP \
This button only appears during encryption/decryption; click it to stop the current crypto task.
- CLR LOG \
This button will clear the outputs seen in the LOG tab during encryption and decryption.

## [FILE TREE]

- A file tree viewer can be seen to the left of the window after login. This tree recursively lists all your files inside its respective tool panel. You can encrypt/decrypt the files from this file tree by selecting the desired files then dragging it to the DROP tab across the GUI, you can also double click on a single file to open it. Single clicking on a file will display its creation date and filesize at the bottom of the file tree panel, and selecting multiple files at once will give the sum of all the selected file sizes.

## [TABBED PANELS]

- DROP \
The tab is only enabled while the STORE panel is active. You can drag and drop any files from your computer into the panel of this tab and it will automatically encrypt or decrypt (dependent on the file type) in its current directory. This is useful if you want to encrypt or decrypt only a few files to work on.
If you are on the N-BOX panel, the DROP tab will instead only accept 1 .i-cc file at a time, and move it into the n-box folder.
- LOG \
The second tab of the pane logs each name of the file being encrypted and decrypted, along with the time that the crypto task was complete. It is automatically selected during encryption and decryption tasks.
- ABOUT \
Copyright information, contact information and liability clauses can be found here.
- HELP \
This tab serves to provide more information on the tools and fields offered by i-ncript. This is the tab that you are currently on.

These components are active throughout all 3 panels, now we will go over some that are exlusive to its tool panel.

## _[STORE]_

This panel is connected to the i-ncript folder and the first panel that you will see. It is your personal encryption folder that you can use to store data that only you can access.

- HOT FILER \
Hot Filer can be toggled for automatic encryption whenever any new files is dropped into the i-ncript folder. If it detects any new files it will run the Encrypt function the same way as clicking on the Encrypt radio button.
- HIDE FILE \
Hide File can be toggled to hide or unhide every file in the i-ncript folder. It runs after every crypto task.

## _[N-BOX]_

This is the second panel after pressing the STORE button on the bottom left of the UI. This panel is connected to your n-box (inbox) folder, and it has the ability to decrypt .i-cc (specialized encrypted files) files that someone else has sent to you, granted you have the correct credentials.

1. Rather than buttons, you are presented with a list and a password field. The list lists out all the .i-cc files that it found in the n-box folder, and if there is one, you can select it by clicking on the .i-cc file name.
2. You then input the password that the sender has provided to you in faith, and as long as its over 4 characters (any less and the DECRYPT button will not appear) and matches the hash inside the .i-cc file, then that file will be decrypted into a folder with all its contents in readable form.

## _[O-BOX]_

This is the last panel when you press the N-BOX folder, which was previously STORE, and pressing it again will cycle you back to the STORE panel. This panel is connected to your o-box (outbox) folder, and any files you put in this folder can be encrypted and packaged into a .i-cc file (which you can send to someone else just like outbox mail).

1. You must first know the username of the recipient's i-ncript account. It must match exactly in order for them to decrypt it. If you do know, then put it in the first text field.
2. You then create a second password that you will confidentially share with the recipient, and this will be hashed. If there are files in the o-box folder (again, make sure you intend for ALL the files and folders in o-box to go to this person because it will package everything inside this folder) then it will be neatly packaged into a .i-cc file for you to email.

## __HOTKEYS__

- SHIFT+E \
This will run the encryption task on the i-ncript folder, similar to clicking the ENCRYPT radio button.
- SHIFT+D \
This will run the decryption task on the i-ncript folder, similar to clicking the DECRYPT radio button.
- SHIFT+S \
This will stop the active encryption/decryption task, similar to clicking the STOP button.
- SHIFT+X \
This will clear the output log on the tabbed pane, similar to clicking CLR LOG.
- SHIFT+F \
This will run hot filer function, similar to clicking on the HOT FILER button.
- SHIFT+SPACE \
This will run the file hider function, similar to clicking on the HIDE FILE button.
- V \
This will update the active file tree.
- ENTER \
This will cycle through the 3 tool panels, STORE, N-BOX, and O-BOX.
- 1,2,3, and 4 \
These number keys will switch to the corresponding tabbed panel such as DROP, LOG, etc.

# <p align="center">CONTACT</p>
### For more information about this app, contact info@i-comit.com (you may delete this README file)

