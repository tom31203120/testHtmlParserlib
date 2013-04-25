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
		// ͨ�� HTMLParser ��ȡ�Ƽ�������Ŀ�б������ postTitleList ��
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
		// ��ʼ�� ListView �� adapter ����� Android �����ϵ���ʾ
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); 
		if(postTitleList != null && postTitleList.size() > 0) { 
			for(String title : postTitleList) { 
				// �� postTitleList �����������ʾ�� listview ��
				adapter.add(title); 
			} 
		} 
		setListAdapter(adapter); 
	}   
	
	private class DownloadFilesTask extends AsyncTask<String, Integer, ArrayList<String>> {
		protected ArrayList<String> doInBackground(String... urls) {
			//final String DW_HOME_PAGE_URL = "http://www.ibm.com/developerworks/cn"; 
			ArrayList<String> pTitleList = new ArrayList<String>(); 
			// ���� html parser ���󣬲�ָ��Ҫ������ҳ�� URL �ͱ����ʽ
			try {
				htmlParser = new Parser(urls[0]);
				htmlParser.setEncoding("UTF-8");
				String postTitle = ""; 
				// ��ȡָ���� div �ڵ㣬�� <div> ��ǩ�����Ҹñ�ǩ���������� id ֵΪ��tab1��
				NodeList divOfTab1 = htmlParser.extractAllNodesThatMatch( 
						new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "tab1")));

				if(divOfTab1 != null && divOfTab1.size() > 0) { 
					// ��ȡָ�� div ��ǩ���ӽڵ��е� <li> �ڵ�
					NodeList itemLiList = divOfTab1.elementAt(0).getChildren().extractAllNodesThatMatch 
							(new TagNameFilter("li"), true);

					if(itemLiList != null && itemLiList.size() > 0) { 
						for(int i = 0; i < itemLiList.size(); ++i) { 
							// �� <li> �ڵ���ӽڵ��л�ȡ Link �ڵ�
							NodeList linkItem = itemLiList.elementAt(i).getChildren().extractAllNodesThatMatch 
									(new NodeClassFilter(LinkTag.class),true); 
							if(linkItem != null && linkItem.size() > 0) { 
								// ��ȡ Link �ڵ�� Text����ΪҪ��ȡ���Ƽ����µ���Ŀ����
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



