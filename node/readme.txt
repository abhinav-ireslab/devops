//FOR LINUX
apt-get install libzmq3-dev build-essential

//FOR WINDOWS
npm install --global windows-build-tools

// COMMON
npm install
node config.js
npm start

// Running in debug mode
node --inspect app.js

// Chrome as Inspector Client
chrome://inspect