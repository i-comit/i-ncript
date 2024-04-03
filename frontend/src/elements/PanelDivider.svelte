<script lang="ts">
</script>

<div class="container">
    <div class="panel">
        <div class="inverted-border-radius left"></div>
        <div
            class="icon"
            style="display: flex; flex-direction: column; position: absolute; z-index: 20"
        >
            <div class="icon__home">
                <ion-icon name="home"></ion-icon>
            </div>
            <div class="icon__account">
                <ion-icon name="person"></ion-icon>
            </div>
            <div class="icon__settings">
                <ion-icon name="settings"></ion-icon>
            </div>
        </div>

        <div class="inverted-border-radius right"></div>
    </div>
</div>

<style lang="scss">
    $panel-width: 150px;

    $bg: #f66969;
    $element-height: 55px;
    $element-width: 75px;
    $element-box-shadow: 0 25px 0 $bg;
    $element-box-shadow-inverse: 0 -25px 0 $bg;

    .panel {
        height: 300px;
        width: $panel-width;
        background-color: green;
        position: relative; // Ensure .inverted-border-radius positions relative to .panel
    }

    .inverted-border-radius {
        position: absolute;
        height: 150px;
        width: $panel-width;
        border-radius: 25px 0 0 25px;

        &.left,
        &.right {
            // Combine common pseudo-element styles
            &::before,
            &::after {
                content: "";
                position: absolute;
                background-color: transparent;
                height: $element-height;
                width: $element-width;
            }

            &::before {
                bottom: -215px;
                box-shadow: $element-box-shadow-inverse;
            }

            &::after {
                top: -55px;
                box-shadow: $element-box-shadow;
            }
        }

        // Apply side-specific styles using nesting
        &.right {
            &::before {
                right: 0px;
                border-top-right-radius: 50%;
            }

            &::after {
                right: 0px;
                border-bottom-right-radius: 50%;
            }
        }

        &.left {
            &::before {
                left: 0px;
                border-top-left-radius: 50%;
            }

            &::after {
                left: 0px;
                border-bottom-left-radius: 50%;
            }
        }
    }

    :root {
        --primary-light: #8abdff;
        --primary: #6d5dfc;
        --primary-dark: #5b0eeb;

        --white: #ffffff;
        --greyLight-1: #e4ebf5;
        --greyLight-2: #c8d0e7;
        --greyLight-3: #bec8e4;
        --greyDark: #9baacf;
    }

    $shadow:
        0.3rem 0.3rem 0.6rem var(--greyLight-2),
        -0.2rem -0.2rem 0.5rem var(--white);
    $inner-shadow:
        inset 0.2rem 0.2rem 0.5rem var(--greyLight-2),
        inset -0.2rem -0.2rem 0.5rem var(--white);

    /*  ICONS  */
    .icon {
        grid-column: 3 / 4;
        grid-row: 4 / 5;
        display: flex;
        justify-content: space-between;
        &__account,
        &__home,
        &__settings {
            width: 4rem;
            height: 4rem;
            border-radius: 50%;
            box-shadow: $shadow;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 2rem;
            cursor: pointer;
            color: var(--greyDark);
            background-color: blue;
            transition: all 0.5s ease;

            &:active {
                box-shadow: $inner-shadow;
                color: var(--primary);
            }
            &:hover {
                color: var(--primary);
            }
        }
    }
</style>
