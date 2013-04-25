package com.example.testhtmlparserlib;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity { 
	private ArrayAdapter<String> adapter;
	private Parser htmlParser; 
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main); 
		List<String> postTitleList = new ArrayList<String>(); 
		// 通过 HTMLParser 获取推荐文章题目列表，存放于 postTitleList 中
		final String DW_HOME_PAGE_URL = "http://www.ibm.com/developerworks/cn"; 
		DownloadFilesTask dfTask = new DownloadFilesTask();
		dfTask.execute(DW_HOME_PAGE_URL);
		try {
			postTitleList = dfTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 初始化 ListView 的 adapter 完成在 Android 界面上的显示
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); 
		if(postTitleList != null && postTitleList.size() > 0) { 
			for(String title : postTitleList) { 
				// 将 postTitleList 里面的内容显示在 listview 中
				adapter.add(title); 
			} 
		} 
		setListAdapter(adapter); 
	}   
	
	private class DownloadFilesTask extends AsyncTask<String, Integer, ArrayList<String>> {
		protected ArrayList<String> doInBackground(String... urls) {
			//final String DW_HOME_PAGE_URL = "http://www.ibm.com/developerworks/cn"; 
			ArrayList<String> pTitleList = new ArrayList<String>(); 
			// 创建 html parser 对象，并指定要访问网页的 URL 和编码格式
			try {
				htmlParser = new Parser(urls[0]);
				htmlParser.setEncoding("UTF-8");
				String postTitle = ""; 
				// 获取指定的 div 节点，即 <div> 标签，并且该标签包含有属性 id 值为“tab1”
				NodeList divOfTab1 = htmlParser.extractAllNodesThatMatch( 
						new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "tab1")));

				if(divOfTab1 != null && divOfTab1.size() > 0) { 
					// 获取指定 div 标签的子节点中的 <li> 节点
					NodeList itemLiList = divOfTab1.elementAt(0).getChildren().extractAllNodesThatMatch 
							(new TagNameFilter("li"), true);

					if(itemLiList != null && itemLiList.size() > 0) { 
						for(int i = 0; i < itemLiList.size(); ++i) { 
							// 在 <li> 节点的子节点中获取 Link 节点
							NodeList linkItem = itemLiList.elementAt(i).getChildren().extractAllNodesThatMatch 
									(new NodeClassFilter(LinkTag.class),true); 
							if(linkItem != null && linkItem.size() > 0) { 
								// 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
								postTitle = ((LinkTag)linkItem.elementAt(0)).getLinkText(); 
								System.out.println(postTitle); 
								pTitleList.add(postTitle); 
							} 
						} 
					} 
				} 
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return pTitleList;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}
} 



