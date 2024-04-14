<script>
    import { setupPagePClass } from "../../../stores/constantVariables";
</script>

<p class={setupPagePClass}>
    Linked to the generated VAULT folder and the first page you see when logged
    in, the VAULT where you can store your personal files for encryption. The
    files are encrypted based on the unique hash which was generated from your
    login credentials.
</p>
<p class={setupPagePClass}>
    Encryption and decryption is simply clicking the respective buttons in the
    left panel. However, you can also select specific files from the file tree
    in the right panel & encrypt/decrypt only those specific files. It uses a
    file selection system similar to Windows where clicking on an already
    selected file will deselected all your other selections, but you can hold
    down the CTRL key to keep them.
</p>
<p class={`${setupPagePClass} text-center text-lg font-semibold !mb-0`}>HOT FILER</p>
<p class={setupPagePClass}>
    The hot filer tool is a toggle which when enabled, automatically encrypts
    any files that has been added to the VAULT folder.
</p>
