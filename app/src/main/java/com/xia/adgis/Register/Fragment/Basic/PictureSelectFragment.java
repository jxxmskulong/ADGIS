package com.xia.adgis.Register.Fragment.Basic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.kevin.crop.UCrop;
import com.xia.adgis.CropActivity;
import com.xia.adgis.Utils.SelectPicturePopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class PictureSelectFragment extends BaseFragment implements SelectPicturePopupWindow.OnSelectedListener {

    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;

    /**
     * 选择提示 PopupWindow
     */
    private SelectPicturePopupWindow mSelectPicturePopupWindow;
    /**
     * 图片选择的监听回调
     */
    private OnPictureSelectedListener mOnPictureSelectedListener;

    /**
     * 剪切图片
     */
    protected void selectPicture() {
        mSelectPicturePopupWindow.showPopupWindow(mActivity);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //得到文件存储路径
        //String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        //String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "cropImage.jpeg");
        String filename = "cropImage.jpeg";
        mDestinationUri = Uri.fromFile(new File(activity.getCacheDir(),filename));
        //mDestinationUri = Uri.fromFile(new File(downloadsDirectoryPath,filename));
        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mDestinationUri));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mSelectPicturePopupWindow = new SelectPicturePopupWindow(mContext);
        mSelectPicturePopupWindow.setOnSelectedListener(this);
    }

    @Override
    public void OnSelected(View v, int position) {
        switch (position) {
            case 0:
                // "拍照"按钮被点击了
                takePhoto();
                break;
            case 1:
                // "从相册选择"按钮被点击了
                pickFromGallery();
                break;
            case 2:
                // "取消"按钮被点击了
                mSelectPicturePopupWindow.dismissPopupWindow();
                break;
        }
    }

    private void takePhoto() {
            mSelectPicturePopupWindow.dismissPopupWindow();

            Intent takeIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            //考虑兼容性
            Uri imageUri; //照片文件临时存储路径
            File temp = new File(mTempPhotoPath);
            if(Build.VERSION.SDK_INT <24){
                imageUri = Uri.fromFile(temp);
            }else {
                imageUri = FileProvider.getUriForFile(mActivity, "com.example.cameraalbumtest.fileprovider", temp);
            }
            //下面这句指定调用相机拍照后的照片存储的路径
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
        }

    private void pickFromGallery() {
            mSelectPicturePopupWindow.dismissPopupWindow();
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mActivity.RESULT_OK) {
            switch (requestCode) {

                case CAMERA_REQUEST_CODE:   // 调用相机拍照
                    //考虑兼容性
                    Uri imageUri; //照片文件临时存储路径
                    File temp = new File(mTempPhotoPath);
                    if(Build.VERSION.SDK_INT < 24){
                        imageUri = Uri.fromFile(temp);
                    }else {
                        imageUri = FileProvider.getUriForFile(mActivity, "com.example.cameraalbumtest.fileprovider", temp);
                    }
                    startCropActivity(imageUri);
                    break;
                case GALLERY_REQUEST_CODE:  // 直接从相册获取
                    startCropActivity(data.getData());
                    break;
                case UCrop.REQUEST_CROP:    // 裁剪图片结果
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:    // 裁剪图片错误
                    handleCropError(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1024, 1024)
                .withTargetActivity(CropActivity.class)
                .start(getActivity(), this);
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri && null != mOnPictureSelectedListener) {
            Bitmap bitmap = null;
            try {
                //只能使用这种方法得出
                bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //监听回掉
            mOnPictureSelectedListener.onPictureSelected(resultUri, bitmap);
        } else {
            Toast.makeText(mContext, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(mContext, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    /**
     * 设置图片选择的回调监听
     *
     * @param l
     */
    public void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        this.mOnPictureSelectedListener = l;
    }

    /**
     * 图片选择的回调接口
     */
    public interface OnPictureSelectedListener {
        /**
         * 图片选择的监听回调
         *
         * @param fileUri
         * @param bitmap
         */
        void onPictureSelected(Uri fileUri, Bitmap bitmap);
    }


}