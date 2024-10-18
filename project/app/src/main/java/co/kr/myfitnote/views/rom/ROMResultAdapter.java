package co.kr.myfitnote.views.rom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import co.kr.myfitnote.R;

public class ROMResultAdapter extends BaseAdapter {

    private Context context;
    private List<RomResultData> items;

    public ROMResultAdapter(Context context, List<RomResultData> items){
        this.context = context;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public RomResultData getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.rom_result_item, parent, false);
        }

        TextView typeTV = convertView.findViewById(R.id.rom_result_item_type_tv);
        TextView valueTV = convertView.findViewById(R.id.rom_result_item_value_tv);

        RomResultData data = getItem(position);
        typeTV.setText(data.getType());
        valueTV.setText(data.getValue() + "°");

        return convertView;
    }
}
