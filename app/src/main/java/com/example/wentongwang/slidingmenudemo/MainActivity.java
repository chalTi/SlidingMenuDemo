package com.example.wentongwang.slidingmenudemo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private ListView mListview;
    private TextView mContentView;
    private ImageView mBackBtn;
    private SlidingMenu mSlidingMenu;

    private int[] images = {R.drawable.tab_news, R.drawable.tab_read, R.drawable.tab_focus, R.drawable.tab_local,
                            R.drawable.tab_pics, R.drawable.tab_ties, R.drawable.tab_vote, R.drawable.tab_ugc};
    private String[] menuName = {"选项1","选项2","选项3","选项4","选项5","选项6","选项7","选项8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        setContentView(R.layout.activity_main);

        mContentView = (TextView) findViewById(R.id.content);
        mSlidingMenu = (SlidingMenu) findViewById(R.id.mSliding);
        mSlidingMenu.useLeftMenu(true);
        mSlidingMenu.useRightMenu(true);

        mListview = (ListView) findViewById(R.id.left_menu);
        mListview.setAdapter(new MyAdapter());
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置每个Item的点击事件
                mContentView.setText(menuName[position]);
            }
        });

        mBackBtn = (ImageView) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSlidingMenu.isLeftMenuOpened()){
                    mSlidingMenu.closeMenu(SlidingMenu.MENU_LEFT);
                }else{
                    mSlidingMenu.openMenu(SlidingMenu.MENU_LEFT);
                }

            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null){
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_item, null);

                holder = new ViewHolder();
                holder.menuItem = (TextView) convertView.findViewById(R.id.menu);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            //获取左侧图片
            Drawable drawable = getResources().getDrawable(images[position]);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.menuItem.setText(menuName[position]);
            holder.menuItem.setCompoundDrawables(drawable,null,null,null);

            return convertView;
        }
        class ViewHolder {
            TextView menuItem;


        }
    }

}
