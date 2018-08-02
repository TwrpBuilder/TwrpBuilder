## TWRPBuilder App 
[![Build Status](https://travis-ci.org/TwrpBuilder/TwrpBuilder.svg?branch=master)](https://travis-ci.org/TwrpBuilder/TwrpBuilder) [![Crowdin](https://d322cqt584bo4o.cloudfront.net/twrpbuilder/localized.svg)](https://crowdin.com/project/twrpbuilder)
>An Android app that allows you to request TWRP for your (officially unsupported) device. All it needs is some basic info about your device, and through the app, it sends the info to be used for the creation of TWRP Recovery for your device.
### Download
 >[![Download](https://img.shields.io/github/release/TwrpBuilder/TwrpBuilder/all.svg?longCache=true&style=for-the-badge)](https://github.com/TwrpBuilder/TwrpBuilder/releases/latest)
### Requirements
* Google Play Services (latest version)
* BusyBox (for older devices)
* Root access (optional)
### How to use
*	Install the app and login with Google account
* _Rooted device_	
	* Grant Root Access
	*	Select `Backup Recovery` & `Upload Backup`
* _Non Rooted device_
	* Get any working `recovery.img` for your device (from stock ROM or old working TWRP)
	* Select `Backup Recovery` & browse to that `recovery.img`
	* `Generate Backup` & `Upload Backup`
*	You'll get in app notification for updates
### Disclaimer
>This project is not supported or affiliated in any way with the original TWRP (TeamWin Recovery Project).
We are an app that creates the TWRP Recovery for you. The original project is at https://twrp.me/
