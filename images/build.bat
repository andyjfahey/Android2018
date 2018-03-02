@REM uses convert from http://www.imagemagick.org/
convert HTIconSecurity.pdf -background transparent -gravity center -scale 64x64 -extent 96x96 ..\app\src\main\res\drawable-xhdpi\ic_menu_secure.png
@rem convert HTIconEnvelope.pdf -background transparent -gravity center -scale 64x64 -fuzz 0% -fill "#ffffff" -opaque "#ffffff" ..\app\src\main\res\drawable-xhdpi\ic_envelope.png
@rem convert HTIconEnvelope.pdf -background transparent -gravity center -scale 60x60 -fuzz 0% -fill "#ffffff" +opaque "#ffffff" ..\app\src\main\res\drawable-xhdpi\ic_envelope.png
@rem convert HTIconEnvelope.pdf -background transparent -quality 100 -gravity center -scale 76x76 -fill "#ffffff" +opaque "#ffffff" ..\app\src\main\res\drawable-xhdpi\ic_envelope.png
@rem convert HTIconEnvelope.pdf -background transparent -scale 76x76 -fill "#ffffff" -opaque "#454545" ..\app\src\main\res\drawable-xhdpi\ic_envelope.png
@rem convert HTIconEnvelope.pdf -background transparent -scale 76x76 -colorspace Gray -auto-level ..\app\src\main\res\drawable-xhdpi\ic_envelope.png

set PARAMS=-background transparent -gravity center -scale 76x76 -extent 76x76 -fill "#ffffff" +opaque "#ffffff"
convert HTIconStats.pdf %PARAMS%        ..\app\src\main\res\drawable-xhdpi\ic_stats.png
convert HTIconEnvelope.pdf %PARAMS%     ..\app\src\main\res\drawable-xhdpi\ic_envelope.png
convert HTIconStethoscope.pdf %PARAMS%  ..\app\src\main\res\drawable-xhdpi\ic_stethoscope.png
convert HTIconMedication.pdf %PARAMS%   ..\app\src\main\res\drawable-xhdpi\ic_medication.png
convert HTIconSettings.pdf %PARAMS%     ..\app\src\main\res\drawable-xhdpi\ic_settings.png



