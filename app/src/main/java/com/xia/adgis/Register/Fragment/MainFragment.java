package com.xia.adgis.Register.Fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.xia.adgis.GlideProgress.ProgressInterceptor;
import com.xia.adgis.GlideProgress.ProgressListener;
import com.xia.adgis.R;
import com.xia.adgis.Register.Fragment.Basic.PictureSelectFragment;
import com.xia.adgis.Register.RegisterActivity;


import java.io.File;

import butterknife.BindView;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainFragment extends PictureSelectFragment {
    //标志变量，判断是否选择了图片
    public static String s = "1";
    public static String delete = "";
    @BindView(R.id.main_frag_picture_iv)
    CircleImageView mPictureIv;

    @BindView(R.id.back2)
    ImageView back2;

    @BindView(R.id.next_move2)
    Button nextmove;
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initEvents() {
        // 设置图片点击监听
        mPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectPicture();
            }
        });
        // 设置裁剪图片结果监听
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
                mPictureIv.setImageBitmap(bitmap);
                String filePath = fileUri.getEncodedPath();
                String imagePath = Uri.decode (filePath);
                //s = imagePath;
                final ProgressDialog progressDialog = new ProgressDialog(mActivity);
                progressDialog.setMax(100);
                progressDialog.setTitle("上传头像");
                progressDialog.setMessage("加载中");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final BmobFile bmobFile = new BmobFile(new File(imagePath));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            s = bmobFile.getFileUrl();
                            delete = bmobFile.getUrl();
                            //Toast.makeText(mContext,delete, Toast.LENGTH_LONG).show();
                        }else {
                            ToastUtils.showLongToast(mContext, e.getMessage());
                        }
                    }

                    @Override
                    public void onProgress(Integer value) {
                        if(value == 100){
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });

        nextmove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s.equals("1")){
                    ToastUtils.showShortToast(mContext,"请选择图片");
                }else {
                    RegisterActivity.total.incrementProgressBy(100);
                    RegisterActivity.second.setVisibility(View.GONE);
                    RegisterActivity.third.setVisibility(View.VISIBLE);
                    //有进度条的图片加载
                    final ProgressDialog progressDialog = new ProgressDialog(mActivity);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("加载中");
                    ProgressInterceptor.addListener(s, new ProgressListener() {
                        @Override
                        public void onProgress(int progress) {
                            progressDialog.setProgress(progress);
                        }
                    });
                    Glide.with(mActivity)
                            .load(s)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .into(new GlideDrawableImageViewTarget(RegisterActivity.userIcon){
                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    progressDialog.show();
                                }

                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                    super.onResourceReady(resource, animation);
                                    progressDialog.dismiss();
                                    ProgressInterceptor.removeListener(s);
                                }
                            });
                    //RegisterActivity.userIcon.setImageBitmap(mbitmap);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        s = "1";
    }
}
