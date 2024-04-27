// *****************************************************************************
// * This file is part the i-ncript project. It is distributed under the       *
// * GNU General Public License: https://www.gnu.org/licenses/gpl-3.0          *
// * Copyright 2022-present Khiem Luong (@khiemgluong) - All Rights Reserved   *
// *****************************************************************************

import "./app.pcss";
import "./style.css";
import 'flowbite/dist/flowbite.css';

import App from "./App.svelte";

const app = new App({
  target: document.getElementById("app"),
});

export default app;