package co.kr.myfitnote.views.parcel;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelablePointF extends PointF implements Parcelable {
    public ParcelablePointF(float x, float y) {
        super (x, y);
    }

    protected ParcelablePointF(Parcel in) {
        super(in.readFloat(), in.readFloat());
    }

    public static final Creator<ParcelablePointF> CREATOR = new Creator<ParcelablePointF>() {
        @Override
        public ParcelablePointF createFromParcel(Parcel in) {
            return new ParcelablePointF(in);
        }

        @Override
        public ParcelablePointF[] newArray(int size) {
            return new ParcelablePointF[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }
}