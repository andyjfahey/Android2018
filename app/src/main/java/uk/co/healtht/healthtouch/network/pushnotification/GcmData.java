package uk.co.healtht.healthtouch.network.pushnotification;

import android.os.Parcel;
import android.os.Parcelable;

public class GcmData implements Parcelable {
    public Data data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, 0);
    }

    public GcmData() {
    }

    protected GcmData(Parcel in) {
        this.data = in.readParcelable(Data.class.getClassLoader());
    }

    public static final Creator<GcmData> CREATOR = new Creator<GcmData>() {
        public GcmData createFromParcel(Parcel source) {
            return new GcmData(source);
        }

        public GcmData[] newArray(int size) {
            return new GcmData[size];
        }
    };
}
