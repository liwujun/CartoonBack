package cn.yicha.cartoon.entity;

import java.util.List;

import org.apache.lucene.document.Document;

/**
 * 搜索结果，包含搜索的文档列表和总数
 * @author xrx
 * @since 2012-11-08
 */
public class SearchResult {
	
	/** 搜索结果的文档列表 */
	private List<Document> docs;
	
	/** 搜索结果总数 */
	private int count;

	/** 搜索结果的文档列表 */
	public List<Document> getDocs() {
		return docs;
	}
	
	/** 搜索结果的文档列表 */
	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}

	/** 搜索结果总数 */
	public int getCount() {
		return count;
	}
	
	/** 搜索结果总数 */
	public void setCount(int count) {
		this.count = count;
	}
}
