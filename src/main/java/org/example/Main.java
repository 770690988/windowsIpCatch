package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author biubiu
 * @version 1.0 2024/3/13 16:04
 * @Description TODO
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        //开始进行监测
        HashSet<String> ipset = new HashSet<>(16);
        HashSet<String> highRiskIp = new HashSet<>(16);
        System.out.println("获取建立连接的IP列表");
        while (true) {
            try {
                // 创建 ProcessBuilder 对象，并设置要执行的命令
                ProcessBuilder builder = new ProcessBuilder("netstat", "-no");
                // 启动进程
                Process process = builder.start();

                // 获取进程的输入流，用于读取命令输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));

                // 读取命令输出
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("ESTABLISHED")) {
//                        System.out.println(line);
//                    // 分割每行，获取第二部分内容
                        String[] parts = line.split("\\s+", 50);
                        if (parts.length >= 2) {
                            Pattern pattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
                            String secondPart = parts[3];
                            Matcher matcher = pattern.matcher(secondPart);
                            if (matcher.find()){
                                String ip = matcher.group(1);
                                if (ipset.contains(ip)) {
                                    continue;
                                }
                                //不存在
                                ipset.add(ip);
                                System.out.println("新IP：" + ip + " 加入");
                            }

                        }
                    }
                }
                // 等待进程执行完成
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.err.println("执行 netstat 命令时出现错误。");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}