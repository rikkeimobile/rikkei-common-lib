package rikkei.android.sns.lib;

import com.facebook.internal.LoginAuthorizationType;
import com.facebook.internal.Utility;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;

import java.util.Collections;
import java.util.List;

/**
 * Created by tuyenpx on 02/06/2016.
 */
@SuppressWarnings("UnusedDeclaration")
public class RkFaceBookLoginProperty {
    private DefaultAudience defaultAudience = DefaultAudience.FRIENDS;
    private List<String> permissions = Collections.emptyList();
    private LoginAuthorizationType authorizationType = null;
    private LoginBehavior loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK;

    public void setDefaultAudience(DefaultAudience defaultAudience) {
        this.defaultAudience = defaultAudience;
    }

    public DefaultAudience getDefaultAudience() {
        return defaultAudience;
    }

    public void setReadPermissions(List<String> permissions) {

        if (LoginAuthorizationType.PUBLISH.equals(authorizationType)) {
            throw new UnsupportedOperationException("Cannot call setReadPermissions after " +
                    "setPublishPermissions has been called.");
        }
        this.permissions = permissions;
        authorizationType = LoginAuthorizationType.READ;
    }

    public void setPublishPermissions(List<String> permissions) {

        if (LoginAuthorizationType.READ.equals(authorizationType)) {
            throw new UnsupportedOperationException("Cannot call setPublishPermissions after " +
                    "setReadPermissions has been called.");
        }
        if (Utility.isNullOrEmpty(permissions)) {
            throw new IllegalArgumentException(
                    "Permissions for publish actions cannot be null or empty.");
        }
        this.permissions = permissions;
        authorizationType = LoginAuthorizationType.PUBLISH;
    }

    public LoginAuthorizationType getAuthorizationType() {
        return authorizationType;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void clearPermissions() {
        permissions = null;
        authorizationType = null;
    }

    public void setLoginBehavior(LoginBehavior loginBehavior) {
        this.loginBehavior = loginBehavior;
    }

    public LoginBehavior getLoginBehavior() {
        return loginBehavior;
    }
}
