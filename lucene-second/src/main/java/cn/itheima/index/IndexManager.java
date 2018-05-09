package cn.itheima.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.itheima.dao.BookDao;
import cn.itheima.dao.impl.BookDaoImpl;
import cn.itheima.po.Book;

/**
 * 索引管理类
 * @author Administrator
 *
 */
public class IndexManager {

	// 定义索引库位置常量
		public static final String INDEX_PATH_DIRECTORY="E:\\heimaEveryday\\lucene&solr_day_01\\lucene&solr_day_01\\index\\";
	
	/**
	 * 索引流程实现
	 */
	@Test
	public void createIndex() throws IOException{
		
//	1.	采集数据
		BookDao bookDao = new BookDaoImpl();
		List<Book> list = bookDao.queryBookList();
		
//	2.	建立文档对象（Document）
		List<Document> docList = new ArrayList<Document>();
		for (Book book : list) {
			//创建文档对象
			Document doc = new Document();
			
			//给文档对象添加域
			//图书id
			/**
			 * 方法:add,它的作用是把域添加到文档对象中
			 * 参数:field:域
			 * 
			 * TextField:文本域
			 * 参数:
			 * 		name:域的名称
			 * 		value:域值
			 * 		store:指定是否把域值存储到文档中
			 */
			/**
			 * 图书Id
				是否分词：不需要分词
				是否索引：需要索引
				是否存储：需要存储
				
				--StringField
			 */
			doc.add(new StringField("bookId", book.getId()+"", Store.YES));
			
//			图书名称
			/**
			 * 图书名称
				是否分词：需要分词
				是否索引：需要索引
				是否存储：需要存储
				
				--TextField
			 */
			doc.add(new TextField("bookName", book.getBookName(), Store.YES));
//			图书价格
			/**
			 * 图书价格
				是否分词：（lucene对于数值型的Field，使用内部分词）
				是否索引：需要索引
				是否存储：需要存储
				
				--DoubleField
			 */
			doc.add(new DoubleField("bookPrice", book.getPrice(), Store.YES));
//			图书图片
			/**
			 * 图书图片
				是否分词：不需要分词
				是否索引：不需要索引
				是否存储：需要存储
				
				--StoredField
			 */
			doc.add(new StoredField("bookPic", book.getPic()));
//			图书描述
			/**
			 * 图书描述
				是否分词：需要分词
				是否索引：需要索引
				是否存储：不需要存储
				
				--TextField
			 */
			doc.add(new TextField("bookDesc", book.getBookdesc(), Store.NO));
			
			docList.add(doc);
		}

//	3.	建立分析器对象（Analyzer），用于分词
//		Analyzer analyzer = new StandardAnalyzer();
		//使用ik中文分词器
		Analyzer analyzer = new IKAnalyzer();
//	4.	建立索引库配置对象（IndexWriterConfig）
		/**
		 * 参数:
		 * 		matchVersion:指定当前只用的luncene版本
		 * 		analyzer:指定当前使用的分析器对象
		 */
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
//	5.	建立索引库目录对象（Directory），指定索引库的位置
		File path = new File("E:\\heimaEveryday\\lucene&solr_day_01\\lucene&solr_day_01\\index\\");
		Directory directory = FSDirectory.open(path);
//	6.	建立索引库的操作对象（IndexWriter），操作索引库
		IndexWriter writer = new IndexWriter(directory, iwc);
//	7.	使用IndexWriter对象，把Document对象写入索引库
		for (Document doc : docList) {
			/**
			 * addDocument方法:把文档对象写入索引库
			 */
			writer.addDocument(doc);
		}
//	8.	释放资源
		writer.close();
	}


	/**
	 * 检索流程实现
	 * @throws Exception 
	 */
	@Test
	public void readIndex() throws Exception{
//		1.	建立分析器对象（Analyzer），用于分词
//		Analyzer analyzer = new StandardAnalyzer();
		//使用ik中文分词器
		Analyzer analyzer = new IKAnalyzer();
//		2.	建立查询对象（Query）
		// 2.1.建立查询解析器对象【bookName:java】
		/**
		 * 参数一:默认搜索域
		 * 参数二:使用的分析器对象
		 */
		QueryParser qp = new QueryParser("bookName", analyzer);
		// 2.2.使用查询解析器对象，解析表达式，实例化query对象
		Query query = qp.parse("bookName:java");
//		3.	建立索引库的目录对象（Directory），指定索引库的位置
		FSDirectory directory = FSDirectory.open(new File("E:\\heimaEveryday\\lucene&solr_day_01\\lucene&solr_day_01\\index\\"));
//		4.	建立索引的读取对象（IndexReader），把索引数据读取到内存中
		IndexReader reader = DirectoryReader.open(directory);
//		5.	建立索引搜索对象（IndexSearcher），执行搜索，返回搜索结果集（TopDocs）
		IndexSearcher searcher = new IndexSearcher(reader);
//		6.	处理结果集
		/**
		 * 方法search:执行搜索
		 * 参数一:查询对象
		 * 参数二:指定搜索结果排序后的前n个
		 */
		TopDocs topDocs = searcher.search(query, 10);
		//6.1实际查询到的结果数量
		System.out.println("实际查询到的结果数量:"+topDocs.totalHits);
		//6.2获取查询的结果数组
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		// 增加分页搜索处理==========================start
				// 1.当前页
				//int page=1;//默认查询第一页
				int page=2;
				
				// 2.每一页显示大小
				int pageSize=2;
				
				// 3.当前页的开始索引
				int start=(page-1)*pageSize;
				
				// 4.当前页的结束索引
				// 正常情况下：start+pageSize
				// 最后一页：start+pageSize 和scoreDocs.length取最小值
				int end=Math.min(start+pageSize, scoreDocs.length);
				
		// 增加分页搜索处理==========================end
		
		for (ScoreDoc sd : scoreDocs) {
			System.out.println("----------华丽丽的分割线------------");
			//获取文档id和分值
			int docId = sd.doc;
			float score = sd.score;
			System.out.println("文档的id:"+docId+"文档的分值:"+score);
			
			//根据文档id获取文档的数据(相当于关系数据库表中,根据主键值查询)
			Document doc = searcher.doc(docId);
			System.out.println("图书id:"+doc.get("bookId"));
			System.out.println("图书名称:"+doc.get("bookName"));
			System.out.println("图书价格:"+doc.get("bookPrice"));
			System.out.println("图书图片:"+doc.get("bookPic"));
			System.out.println("图书描述:"+doc.get("bookDesc"));
		}
//		7.	释放资源
		reader.close();
	}
	
	/**
	 * 根据Term删除索引
	 * @throws IOException 
	 */
	@Test
	public void deleteIndexByTerm() throws IOException{
//		1.建立分析器对象（Analyzer），用于分词
		Analyzer analyzer = new IKAnalyzer();
		
//		2.建立索引库的配置对象（IndexWriterConfig），配置索引库
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
//		3.建立索引库的目录对象（Directory），指定索引库的位置
		Directory directory = FSDirectory.open(new File(INDEX_PATH_DIRECTORY));
		
//		4.建立索引库操作对象（IndexWriter），操作索引库
		IndexWriter writer = new IndexWriter(directory, iwc);
		
//		5.建立条件对象（Term）
		/**
		 * delete from user where id=1;
		 * 
		 *需求：删除图书名称域中包含有lucene的图书
		 */
		Term term = new Term("bookName", "lucene");
		
//		6.使用IndexWriter对象，执行删除
		writer.deleteDocuments(term);
		
//		7.释放资源
		writer.close();
	}

	/**
	 * 删除全部索引
	 * @throws IOException 
	 */
	@Test
	public void deleteAllIndex() throws IOException{
//		1.建立分析器对象（Analyzer）,用于分词
		Analyzer analyzer = new IKAnalyzer();
		
//		2.建立索引库的配置对象（IndexWriterConfig），配置索引库
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
//		3.建立索引库的目录对象（Directory），指定索引库位置
		Directory directory = FSDirectory.open(new File(INDEX_PATH_DIRECTORY));
		
//		4.建立索引库的操作对象（IndexWriter），操作索引库
		IndexWriter writer = new IndexWriter(directory, iwc);
		
//		5.使用IndexWriter对象，执行删除
		writer.deleteAll();
		
//		6.释放资源
		writer.close();
	}

	/**
	 * 更新索引
	 * @throws IOException 
	 */
	@Test
	public void updateIndex() throws IOException{
//		1.建立分析器对象（Analyzer），用于分词
		Analyzer analyzer = new IKAnalyzer();
		
//		2.建立索引库的配置对象（IndexWriterConfig），配置索引库
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
//		3.建立索引库的目录对象（Directory），指定索引库的位置
		Directory directory = FSDirectory.open(new File(INDEX_PATH_DIRECTORY));
		
//		4.建立索引库的操作对象（IndexWriter），操作索引库
		IndexWriter writer = new IndexWriter(directory, iwc);
		
//		5.建立文档对象（Document）
		Document doc = new Document();
		
		doc.add(new TextField("id", "9527", Store.YES));
//		doc.add(new TextField("name", "mybaits and spring and springmvc", Store.YES));
		
		// 测试更新
		doc.add(new TextField("name", "hibernate and Struts2 and mybaits and spring and springmvc", Store.YES));
		
//		6.建立条件对象（Term）
		Term term = new Term("name", "mybatis");
		
//		7.使用IndexWriter对象，执行更新
		writer.updateDocument(term, doc);
		
//		8.释放资源
		writer.close();
	}

}



























