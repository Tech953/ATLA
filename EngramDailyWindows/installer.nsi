Unicode true
!include "MUI2.nsh"
!include "x64.nsh"
Name "Engram Daily · Jade"
OutFile "${OUTPUT}"
InstallDir "$LOCALAPPDATA\Programs\Engram Daily"
InstallDirRegKey HKCU "Software\EngramDaily" "InstallDir"
RequestExecutionLevel user
SetCompressor /SOLID lzma
!define MUI_ICON "${ICON}"
!define MUI_UNICON "${ICON}"
!define MUI_ABORTWARNING
!define MUI_FINISHPAGE_RUN "$INSTDIR\Engram Daily.exe"
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_LANGUAGE "English"
Function .onInit
  ${IfNot} ${RunningX64}
    MessageBox MB_OK|MB_ICONSTOP "This installer requires 64-bit Windows."
    Abort
  ${EndIf}
FunctionEnd
Section "Install"
  SetShellVarContext current
  SetOutPath "$INSTDIR"
  ClearErrors
  File /r "${APPDIR}/*"
  ${If} ${Errors}
    MessageBox MB_OK|MB_ICONSTOP "Some files could not be installed. Close Engram Daily and run Setup again."
    Abort
  ${EndIf}
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  CreateShortcut "$SMPROGRAMS\Engram Daily.lnk" "$INSTDIR\Engram Daily.exe" "" "$INSTDIR\resources\runtime\icon.ico"
  CreateShortcut "$DESKTOP\Engram Daily.lnk" "$INSTDIR\Engram Daily.exe" "" "$INSTDIR\resources\runtime\icon.ico"
  WriteRegStr HKCU "Software\EngramDaily" "InstallDir" "$INSTDIR"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\EngramDaily" "DisplayName" "Engram Daily · Jade"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\EngramDaily" "DisplayVersion" "0.2.1"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\EngramDaily" "UninstallString" '"$INSTDIR\Uninstall.exe"'
  WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\EngramDaily" "NoModify" 1
  WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\EngramDaily" "NoRepair" 1
SectionEnd
Section "Uninstall"
  SetShellVarContext current
  !include "${UNINSTALL_INCLUDE}"
  Delete "$SMPROGRAMS\Engram Daily.lnk"
  Delete "$DESKTOP\Engram Daily.lnk"
  DeleteRegKey HKCU "Software\EngramDaily"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\EngramDaily"
  DeleteRegValue HKCU "Software\Microsoft\Windows\CurrentVersion\Run" "Engram Daily"
  Delete "$INSTDIR\Uninstall.exe"
  RMDir "$INSTDIR"
SectionEnd
