package cn.yicha.cartoon.util;

import cn.yicha.seg.ChineseSegmenter;

/**
 * 分词器入口
 * @author xrx
 * @since 2012-09-18
 */
public class SplitCaller {
	private static ChineseSegmenter segmenter;

	static{
		segmenter = ChineseSegmenter.getGBSegmenter();
	}
	/**
	 * 获取分词后的搜索词
	 */
	public static synchronized String getSplitKeyword(String keyword) {
		return segmenter.segmentLine(keyword," ");
	}
	
	public static void main(String[] args){
		
	}
}
