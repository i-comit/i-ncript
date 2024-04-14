<script>
    import { setupPagePClass } from "../../../stores/constantVariables";
</script>

<p class={setupPagePClass}>
    This is your mailbox, and is linked to the generated M-BOX folder. Here you
    can select files from the file tree to Pack or Open them.
</p>
<p class={`${setupPagePClass} text-center text-lg font-semibold !mb-0`}>
    PACK
</p>
<p class={setupPagePClass}>
    If the last file you selected was a regular file, then you will be presented
    with a username and password field. Here you can put in the recipient
    username from another i-ncript account, and a one time password associated
    only with that packed file. The files you selected will then be zipped and
    encrypted into an .encp file.
</p>
<p class={`${setupPagePClass} text-center text-lg font-semibold !mb-0`}>
    OPEN
</p>
<p class={setupPagePClass}>
    If the last file you selected had an .encp extension, then it was a packed
    file, and selecting it will prompt you to open it, which requires you to
    input the one time password used to pack the file.
</p>
<p class={setupPagePClass}>
    You can select several files to pack at once, but they must all be regular
    files.
</p>
