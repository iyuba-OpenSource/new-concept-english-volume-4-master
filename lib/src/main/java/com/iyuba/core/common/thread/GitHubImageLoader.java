/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.thread;

import android.content.Context;
import android.widget.ImageView;

import com.iyuba.configation.Constant;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.lil.util.ResLibUtil;

/**
 * 下载图片
 *
 * @author 作者 陈彤
 * @time 2014.4.30
 *
 * 修改于20240417，如需要之前的，请找之前的提交记录
 */
public class GitHubImageLoader {
	private static GitHubImageLoader instance;

	public static GitHubImageLoader getInstance(){
		if (instance==null){
			synchronized (GitHubImageLoader.class){
				if (instance==null){
					instance = new GitHubImageLoader();
				}
			}
		}
		return instance;
	}

	/**
	 * 下载用户头像（中等大小）并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param pic    头像对应的ImageView
	 */
	public void setPic(String userid, ImageView pic) {
		setPic(userid, "big", pic);
	}

	/**
	 * 下载用户头像（中等大小）转换成圆形并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param pic    头像对应的ImageView
	 */
	public void setCirclePic(String userid, ImageView pic) {
		setCirclePic(userid, "big", pic);
	}

	/**
	 * 下载用户头像并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param size   头像大小（small, middle, big）
	 * @param pic    头像对应的ImageView
	 */
	public void setPic(String userid, String size, ImageView pic) {
		String picUrl = "http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + userid
				+ "&size=" + size;

		LibGlide3Util.loadImg(ResLibUtil.getInstance().getContext(), picUrl,0,pic);

	}

	/**
	 * 下载用户头像转换成圆形并在ImageView中显示
	 *
	 * @param userid 用户Id
	 * @param size   头像大小（small, middle, big）
	 * @param pic    头像对应的ImageView
	 */
	public void setCirclePic(String userid, String size, ImageView pic) {
		String picUrl = "http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + userid
				+ "&size=" + size;

		LibGlide3Util.loadImg(ResLibUtil.getInstance().getContext(), picUrl,0,pic);
	}

	/**
	 * 从url下载图片在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setRawPic(String url, ImageView pic, int drawable) {
		LibGlide3Util.loadImg(ResLibUtil.getInstance().getContext(), url,drawable,pic);
	}

	/**
	 * 从url下载图片（默认15度圆角）并在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setPic(String url, ImageView pic, int drawable) {
		setPic(url, pic, drawable, 15);
	}

	/**
	 * 从url下载图片并在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 * @param degree   图片边缘圆角角度
	 */
	public void setPic(String url, ImageView pic, int drawable, int degree) {
		LibGlide3Util.loadImg(ResLibUtil.getInstance().getContext(), url,0,pic);
	}

	/**
	 * 从url下载图片转换成圆形并在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setCirclePic(String url, ImageView pic, int drawable) {
		setPic(url, pic, drawable, 90);
	}
}
