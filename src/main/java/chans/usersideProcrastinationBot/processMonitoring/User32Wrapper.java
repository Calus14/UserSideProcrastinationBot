package chans.usersideProcrastinationBot.processMonitoring;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;

/**
 * Interface to wrap the user32.dll so that the service can call windows calls
 */
public interface User32Wrapper extends Library {
    User32Wrapper Instance = (User32Wrapper) Native.loadLibrary("user32.dll", User32Wrapper.class);

    int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);

    int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);

    WinDef.HWND GetForegroundWindow();
}
