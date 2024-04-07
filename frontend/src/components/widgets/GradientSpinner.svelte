<div class="spinner-box">
    <div class="circle-border">
        <div class="circle-core"></div>
    </div>
</div>

<style>
    .spinner-box {
        width: 30px;
        height: 30px;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: transparent;
    }

    .circle-border {
        width: 15px;
        height: 15px;
        padding: 3px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 50%;
        background: rgb(63, 249, 220);
        background: linear-gradient(
            0deg,
            rgba(63, 249, 220, 0.1) 33%,
            rgba(63, 249, 220, 1) 100%
        );
        animation: spin 0.8s linear 0s infinite;
    }
    .circle-core {
        width: 100%;
        height: 100%;
        background-color: white;
        border-radius: 50%;
    }
</style>
