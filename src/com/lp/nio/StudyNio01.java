package com.lp.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import org.junit.Test;

public class StudyNio01 {
	/**
	 * 分散读取与聚集写入
	 * @throws IOException 
	 */
	@Test
	public void testScatterAndGatter() throws IOException{
		RandomAccessFile raf=new RandomAccessFile("hugo_new_file.txt", "rw");
		
		//获取通道
		FileChannel fc = raf.getChannel();
		//分散读取
		ByteBuffer buf1 = ByteBuffer.allocate(10);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);
		
		ByteBuffer[] bufs = {buf1,buf2};
		
		fc.read(bufs);
		for(ByteBuffer b : bufs){
			b.flip();
		}
		System.out.println(new String(bufs[0].array(),bufs[0].position(),bufs[0].limit()));
		System.out.println("==========================");
		System.out.println(new String(bufs[1].array(),bufs[0].position(),bufs[1].limit()));
		System.out.println("==========================");
		System.out.println(new String(buf1.array(),0,buf1.limit()));
		System.out.println("==========================");
		System.out.println(new String(buf2.array(),0,buf2.limit()));
		
		RandomAccessFile raf2=new RandomAccessFile("hugo_2.txt", "rw");
		FileChannel fc2=raf2.getChannel();
		fc2.write(bufs);
		
		raf.close();
		raf2.close();
	}
	
	/**
	 * 阻塞式socket通讯
	 * 上传文件到服务器
	 * @throws IOException 
	 */
	@Test
	public void tcpBlockingServer() throws IOException{
		ServerSocketChannel ssChannel=ServerSocketChannel.open();
		
		FileChannel fChannel=FileChannel.open(Paths.get("hugo_3.txt"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		
		ssChannel.bind(new InetSocketAddress(8888));
		
		SocketChannel sChannel = ssChannel.accept();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while((sChannel.read(buf))!=-1){
			buf.flip();
			fChannel.write(buf);
			buf.clear();
		}
		
		buf.put("接受完毕".getBytes());
		buf.flip();
		sChannel.write(buf);
		
		sChannel.close();
		fChannel.close();
		ssChannel.close();
	}
	@Test
	public void tcpBlockingClient() throws IOException{
		SocketChannel sChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
		
		FileChannel fChannel=FileChannel.open(Paths.get("hugo_2.txt"), StandardOpenOption.READ);
		
		ByteBuffer buf=ByteBuffer.allocate(1024);
		
		while(fChannel.read(buf)>0){
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		sChannel.shutdownOutput();
		
		int len=0;
		while((len=sChannel.read(buf))!=-1){
			buf.flip();
			System.out.println(new String(buf.array(),0,len));
			buf.clear();
		}
		fChannel.close();
		sChannel.close();
	}
	/**
	 * 阻塞式通讯
	 * @throws IOException 
	 */
	@Test
	public void tcpBlockServer() throws IOException{
		ServerSocketChannel ssChannel=ServerSocketChannel.open();
		ssChannel.bind(new InetSocketAddress(8888));
		SocketChannel sChannel = ssChannel.accept();
		
		ByteBuffer buf=ByteBuffer.allocate(1024);
		int len=0;
		while((len=sChannel.read(buf))!=-1){
			buf.flip();
			System.out.println(new String(buf.array(),0,len));
			buf.clear();
		}
	}
	@Test
	public void tcpBlockClent() throws IOException{
		SocketChannel sChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
		
		ByteBuffer buf=ByteBuffer.allocate(1024);
		
		Scanner sc=new Scanner(System.in);
		
		while(sc.hasNext()){
			buf.put(sc.next().getBytes());
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		sChannel.shutdownOutput();
	}

}
