package uk.co.healtht.healthtouch.network.pushnotification;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable {
    public String created_at;
    public String link;
    public String message;
    public String push_type;
    public String resourceURI;
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.created_at);
        dest.writeString(this.link);
        dest.writeString(this.message);
        dest.writeString(this.push_type);
        dest.writeString(this.resourceURI);
        dest.writeString(this.type);
    }

    public Data() {
    }

    protected Data(Parcel in) {
        this.created_at = in.readString();
        this.link = in.readString();
        this.message = in.readString();
        this.push_type = in.readString();
        this.resourceURI = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
