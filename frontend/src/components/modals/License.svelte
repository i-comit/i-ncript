<script>
    import NeuButton from "../widgets/NeuButton.svelte";
    import { Modal, GradientButton } from "flowbite-svelte";
    let modalBool = false;
    let modalPBodyClass =
        "text-xs leading-relaxed text-primary-100 dark:text-primary-200 mb-2";
</script>

<GradientButton
    color="cyan"
    on:click={() => (modalBool = true)}
    class="mx-3 h-5 mt-2 outline outline-1 border-0">GNU GPLv3</GradientButton
>

<Modal
    bind:open={modalBool}
    autoclose
    outsideclose
    dialogClass="fixed top-0 start-0 end-0 h-modal z-50 w-full p-3.5 flex"
    backdropClass="fixed inset-0 z-40 bg-gray-600 bg-opacity-60 dark:bg-opacity-80"
    bodyClass="bg-primary-400 dark:bg-primary-300 text-primary-100 dark:text-primary-200 rounded-lg"
>
    <div class="license-modal overflow-y-auto p-1">
        <p class="text-center text-md">GNU GENERAL PUBLIC LICENSE</p>
        <p class="text-center text-md">Version 3, 29 June 2007</p>
        <p class={modalPBodyClass}>
            Copyright (C) 2007 Free Software Foundation, Inc. <a
                class="whitespace-nowrap"
                href="https://fsf.org"
                >&lt; https://fsf.org &gt;
            </a>
        </p>
        <p class={modalPBodyClass}>
            Everyone is permitted to copy and distribute verbatim copies of this
            license document, but changing it is not allowed.
        </p>
        <p class="text-center text-md">Preamble</p>

        <p class={modalPBodyClass}>
            The GNU General Public License is a free, copyleft license for
            software and other kinds of works.
        </p>
        <p class={modalPBodyClass}>
            The licenses for most software and other practical works are
            designed to take away your freedom to share and change the works. By
            contrast, the GNU General Public License is intended to guarantee
            your freedom to share and change all versions of a program--to make
            sure it remains free software for all its users. We, the Free
            Software Foundation, use the GNU General Public License for most of
            our software; it applies also to any other work released this way by
            its authors. You can apply it to your programs, too.
        </p>
        <p class={modalPBodyClass}>
            When we speak of free software, we are referring to freedom, not
            price. Our General Public Licenses are designed to make sure that
            you have the freedom to distribute copies of free software (and
            charge for them if you wish), that you receive source code or can
            get it if you want it, that you can change the software or use
            pieces of it in new free programs, and that you know you can do
            these things.
        </p>
        <p class={modalPBodyClass}>
            To protect your rights, we need to prevent others from denying you
            these rights or asking you to surrender the rights. Therefore, you
            have certain responsibilities if you distribute copies of the
            software, or if you modify it: responsibilities to respect the
            freedom of others.
        </p>
        <p class={modalPBodyClass}>
            For example, if you distribute copies of such a program, whether
            gratis or for a fee, you must pass on to the recipients the same
            freedoms that you received. You must make sure that they, too,
            receive or can get the source code. And you must show them these
            terms so they know their rights.
        </p>
        <p class={modalPBodyClass}>
            Developers that use the GNU GPL protect your rights with two steps:
            (1) assert copyright on the software, and (2) offer you this License
            giving you legal permission to copy, distribute and/or modify it.
        </p>
        <p class={modalPBodyClass}>
            For the developers' and authors' protection, the GPL clearly
            explains that there is no warranty for this free software. For both
            users' and authors' sake, the GPL requires that modified versions be
            marked as changed, so that their problems will not be attributed
            erroneously to authors of previous versions.
        </p>
    </div>
</Modal>

<style>
    p {
        text-align: start;
    }
    .license-modal {
        /* position: absolute; */
        /* bottom: 0; */
        height: 50vh;
        scrollbar-width: none;
        scroll-behavior: smooth;

        /* max-height: 100%; */
        /* overflow-x: hidden;
        overflow-y: auto;
        box-sizing: border-box;*/
    }
</style>
