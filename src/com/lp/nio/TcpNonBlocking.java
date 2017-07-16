package com.lp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import org.junit.Test;

public class TcpNonBlocking {
	@Test
	public void Server() throws IOException{
		ServerSocketChannel ssChannel=ServerSocketChannel.open();
		ssChannel.bind(new InetSocketAddress(8888));
		ssChannel.configureBlocking(false);
		
		Selector s=Selector.open();
		ssChannel.register(s, SelectionKey.OP_ACCEPT);
		while(s.select()>0){
			Set<SelectionKey> selectedKeys=s.selectedKeys();
			Iterator<SelectionKey> it=selectedKeys.iterator();
			while(it.hasNext()){
				SelectionKey sk=it.next();
				if(sk.isAcceptable()){
					SocketChannel sChannel=ssChannel.accept();
					sChannel.configureBlocking(false);
					sChannel.register(s, SelectionKey.OP_READ);
				}else if(sk.isReadable()){
					SocketChannel sChannel = (SocketChannel) sk.channel();
					ByteBuffer buf=ByteBuffer.allocate(1024);
					int len=0;
					while((len=sChannel.read(buf))>0){
						buf.flip();
						System.out.println(new String(buf.array(),0,len));
						buf.clear();
					}
				}
				it.remove();
			}
		}
		
	}
	@Test
	public void Client() throws IOException{
		SocketChannel sChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
		sChannel.configureBlocking(false);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		Scanner sc=new Scanner(System.in);
		while(sc.hasNext()){
			buf.put((new Date().toString()+"\n"+sc.nextLine()).getBytes());
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		sc.close();
		sChannel.close();
	}
}
