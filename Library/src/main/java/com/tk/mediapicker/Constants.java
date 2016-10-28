package com.tk.mediapicker;


/**
 * Created by TK on 2016/9/30.
 */

public final class Constants {
    //照片裁剪
    public static final int CROP_REQUEST = 2333;
    //拍照
    public static final int CAMERA_REQUEST = 2334;
    //默认发起request
    public static final int DEFAULT_REQUEST = 2335;
    //附加功能发起request
    public static final int DEFAULT_PLUS_REQUEST = 2336;

    //回调File结果 flag=true data为单数，false，data为list，仅针对结果
    public static final String RESULT_SINGLE = "result_single";
    public static final String RESULT_DATA = "result_data";
    public static final String AS_SYSTEM = "as_system";

    /**
     * CameraRequest
     */
    public static final class CameraRequestConstants {
        public static final String NEED_CROP = "camera_crop";
    }

    /**
     * AlbumRequest
     */
    public static final class AlbumRequestConstants {
        public static final String AS_SINGLE = "is_single";
        public static final String SHOW_VIDEO = "show_video";
        public static final String CHECK_LIMIT = "check_limit";
        public static final String SHOW_CAMERA = "show_camera";
        public static final String NEED_CROP = "need_crop";

    }

    /**
     * album to 相册预览传递参数
     */
    public static final class PreAlbumConstants {
        public static final int PRE_REQUEST = 2337;
        //预览列表
        public static final String ALBUM_LIST = "album_list";
        //选中列表
        public static final String CHECK_LIST = "check_list";
        //当前index
        public static final String INDEX = "index";
        //选择限制
        public static final String LIMIT = AlbumRequestConstants.CHECK_LIMIT;
        //回调是否完成选择
        public static final String FINISH = "finish";
    }


    /**
     * RreviewRequest
     */
    public static final class PreMediaConstants {
        /**
         * 数据源
         */
        public static final String REQUEST_DATA = "request_date";
        //选择限制
        public static final String INDEX = "index";
    }

    /**
     * FileRequest
     */
    public static final class FileConstants {
        public static final String AS_SINGLE = AlbumRequestConstants.AS_SINGLE;
        public static final String CHECK_LIMIT = AlbumRequestConstants.CHECK_LIMIT;
    }

}
