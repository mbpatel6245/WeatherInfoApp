package com.mbpatel.weatherinfo.utils.permission.callbacks;



import com.mbpatel.weatherinfo.utils.permission.PermissionResult;

import java.util.List;

public interface PermissionListener {
    void onAccepted(PermissionResult permissionResult, List<String> accepted);
    void onDenied(PermissionResult permissionResult, List<String> denied, List<String> foreverDenied);
}
