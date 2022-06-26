# WifiBT-AutoStart-Disabler
Prevent SheildTV from turning on Wifi &amp; Bluetooth automatically after reboot

該應用程式用於關閉AndroidTV的Wifi和藍芽功能，目前測試以SheildTV Pro(2019)為主，其他廠牌的Android TV應當適用。
已發現，SheildTV在設備重啟後會將關閉的Wifi和藍芽再次開啟，且AndroidTV先天上的限制，在功能啟用後的狀態顯示並不明顯且不易控制。
為了解決上述問題，功能上加入Wifi和藍芽開關以及開機時的狀態還原，能夠將Wifi和藍芽重設至關機前的狀態。
由於SheildTV的藍芽自啟程序不易抓取啟動時間，為了保障功能正常運作，程式預設在開機完成的15秒後執行。
