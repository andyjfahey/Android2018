package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HAYTHEM Suissi on 2/27/17.
 */

public class DataSplashBanner {

    @SerializedName("splash_url")
    @Expose
    private String splashUrl;
    @SerializedName("banner_url")
    @Expose
    private String bannerUrl;

    public String getSplashUrl() {
        return splashUrl;
    }

    public void setSplashUrl(String splashUrl) {
        this.splashUrl = splashUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

}