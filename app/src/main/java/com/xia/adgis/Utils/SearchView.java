package com.xia.adgis.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.R;
import com.xia.adgis.Main.DataBase.HistorySqliteHelpter;


/**
 * Created by yetwish on 2015-05-11
 */

public class SearchView extends LinearLayout implements View.OnClickListener {

    /**
     * 清除历史按钮
     */
    public static LinearLayout llSearchEmpty;

    /**
     * 输入框
     */
    public EditText etInput;

    /**
     * 删除键
     */
    private ImageView ivDelete;

    /**
     * 隐藏按钮
     */
    public static  ImageView showOrhide;
    public static boolean isShow = true;
    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 弹出列表
     */
    private ListView lvTips;

    /**
     * 提示adapter （推荐adapter）
     */
    private ArrayAdapter<String> mHintAdapter;

    /**
     * 自动补全adapter 只显示名字
     */
    private ArrayAdapter<String> mAutoCompleteAdapter;

    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);
        initViews();
    }

    private void initViews() {
        etInput = (EditText) findViewById(R.id.search_et_input);
        ivDelete = (ImageView) findViewById(R.id.search_iv_delete);
        showOrhide = (ImageView) findViewById(R.id.hideOrshow);
        lvTips = (ListView) findViewById(R.id.search_lv_tips);
        llSearchEmpty = (LinearLayout) findViewById(R.id.ll_search_empty);

        lvTips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //set edit text
                String text = lvTips.getAdapter().getItem(i).toString();
                etInput.setText(text);
                etInput.setSelection(text.length());
                //hint list view gone and result list view show
                lvTips.setVisibility(View.GONE);
                llSearchEmpty.setVisibility(View.GONE);
                showOrhide.setImageResource(R.drawable.ic_expand_more);
                isShow = false;
                notifyStartSearching(text);
            }
        });

        ivDelete.setOnClickListener(this);
        showOrhide.setOnClickListener(this);
        llSearchEmpty.setOnClickListener(this);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            //当点击搜索按钮时
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    lvTips.setVisibility(GONE);
                    llSearchEmpty.setVisibility(GONE);
                    showOrhide.setImageResource(R.drawable.ic_expand_more);
                    isShow = false;
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });
    }

    /**
     * 通知监听者 进行搜索操作
     * @param text
     */
    private void notifyStartSearching(String text){
        if (mListener != null) {
            mListener.onSearch(etInput.getText().toString());
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置热搜版提示 adapter
     */
    public void setTipsHintAdapter(ArrayAdapter<String> adapter) {
        this.mHintAdapter = adapter;
        if (lvTips.getAdapter() == null) {
            lvTips.setAdapter(mHintAdapter);
        }
    }

    /**
     * 设置自动补全adapter
     */
    public void setAutoCompleteAdapter(ArrayAdapter<String> adapter) {
        this.mAutoCompleteAdapter = adapter;
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
                lvTips.setVisibility(VISIBLE);
                llSearchEmpty.setVisibility(GONE);
                //当文本框文字变化隐藏按钮变化
                showOrhide.setImageResource(R.drawable.ic_expand_less);
                isShow = true;
                if (mAutoCompleteAdapter != null && lvTips.getAdapter() != mAutoCompleteAdapter) {
                    lvTips.setAdapter(mAutoCompleteAdapter);
                }
                //更新autoComplete数据
                if (mListener != null) {
                    mListener.onRefreshAutoComplete(charSequence + "");
                }
            } else {
                ivDelete.setVisibility(GONE);
                showOrhide.setImageResource(R.drawable.ic_expand_more);
                isShow = false;
                if (mHintAdapter != null) {
                    lvTips.setAdapter(mHintAdapter);
                }
                lvTips.setVisibility(GONE);
                llSearchEmpty.setVisibility(GONE);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        HistorySqliteHelpter helpter = new HistorySqliteHelpter(mContext);
        SQLiteDatabase database = helpter.getWritableDatabase();
        switch (view.getId()) {
            case R.id.search_et_input:
                mHintAdapter.clear();
                try {
                    String sql = "select * from history";
                    Cursor cursor = database.rawQuery(sql,null);
                    while (cursor.moveToNext()){
                        mHintAdapter.add(cursor.getString(cursor.getColumnIndex("name")));
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                mHintAdapter.notifyDataSetChanged();
                lvTips.setVisibility(VISIBLE);
                //当提示框无历史数据并且输入框为空，这时候显示为无
                if(mHintAdapter.isEmpty()&&TextUtils.isEmpty(etInput.getText())){
                    isShow = false;
                    showOrhide.setImageResource(R.drawable.ic_expand_more);
                }else if(mAutoCompleteAdapter.isEmpty()&&!TextUtils.isEmpty(etInput.getText())) {
                    isShow = false;
                    showOrhide.setImageResource(R.drawable.ic_expand_more);
                }else {
                    isShow = true;
                    showOrhide.setImageResource(R.drawable.ic_expand_less);
                }
                //历史提示不为为空并且输入框为空显示下拉提示列表
                if(!mHintAdapter.isEmpty()&&TextUtils.isEmpty(etInput.getText())) {
                    llSearchEmpty.setVisibility(VISIBLE);
                }else{
                    llSearchEmpty.setVisibility(GONE);
                }
                break;
            case R.id.search_iv_delete:
                etInput.setText("");
                ivDelete.setVisibility(GONE);
                llSearchEmpty.setVisibility(GONE);
                break;
            case R.id.hideOrshow:
                if(mHintAdapter.isEmpty()&&TextUtils.isEmpty(etInput.getText())){
                    isShow = false;
                    showOrhide.setImageResource(R.drawable.ic_expand_more);
                }else if(mAutoCompleteAdapter.isEmpty()&&!TextUtils.isEmpty(etInput.getText())) {
                    isShow = false;
                    showOrhide.setImageResource(R.drawable.ic_expand_more);
                }else {
                    if (isShow) {
                        lvTips.setVisibility(GONE);
                        llSearchEmpty.setVisibility(GONE);
                        showOrhide.setImageResource(R.drawable.ic_expand_more);
                        isShow = false;
                    } else {
                        lvTips.setVisibility(VISIBLE);
                        if (!mHintAdapter.isEmpty() && TextUtils.isEmpty(etInput.getText())) {
                            llSearchEmpty.setVisibility(VISIBLE);
                        } else {
                            llSearchEmpty.setVisibility(GONE);
                        }
                        showOrhide.setImageResource(R.drawable.ic_expand_less);
                        isShow = true;
                    }
                }
                break;
            case R.id.ll_search_empty:
                mHintAdapter.clear();
                llSearchEmpty.setVisibility(GONE);
                showOrhide.setImageResource(R.drawable.ic_expand_more);
                isShow = false;
                try {
                    String sql = "delete from history";
                    database.execSQL(sql);
                    Toast.makeText(getContext(),"清空成功",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(getContext(),"清空失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {

        /**
         * 更新自动补全内容
         *
         * @param text 传入补全后的文本
         */
        void onRefreshAutoComplete(String text);

        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);

    }

}
