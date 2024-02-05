import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DNSMessage {
    // DNS消息的组成部分
    private DNSHeader header; // 消息头
    private DNSQuestion[] questions; // 查询问题数组
    private DNSRecord[] answers; // 应答记录数组
    private DNSRecord[] authorityRecords; // 授权记录数组
    private DNSRecord[] additionalRecords; // 附加记录数组
    private byte[] messageContent; // 消息的原始字节内容

    // 从字节数组解码生成DNSMessage对象的静态方法
    public static DNSMessage decodeMessage(byte[] bytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        DNSMessage message = new DNSMessage();
        message.messageContent = bytes;

        // 分别解码消息头、查询问题、应答记录、授权记录和附加记录
        message.header = DNSHeader.decodeHeader(inputStream);
        message.questions = decodeQuestions(inputStream, message.header.getQuestions(), message);
        message.answers = decodeRecords(inputStream, message.header.getAnswerRRs(), message);
        message.authorityRecords = decodeRecords(inputStream, message.header.getAuthorityRRs(), message);
        message.additionalRecords = decodeRecords(inputStream, message.header.getAdditionalRRs(), message);

        return message;
    }

    // 解码查询问题
    private static DNSQuestion[] decodeQuestions(ByteArrayInputStream inputStream, int count, DNSMessage message) throws IOException {
        DNSQuestion[] questions = new DNSQuestion[count];
        for (int i = 0; i < count; i++) {
            questions[i] = DNSQuestion.decodeQuestion(inputStream, message);
        }
        return questions;
    }

    // 解码DNS记录（应答、授权、附加）
    private static DNSRecord[] decodeRecords(ByteArrayInputStream inputStream, int count, DNSMessage message) throws IOException {
        DNSRecord[] records = new DNSRecord[count];
        for (int i = 0; i < records.length; i++) {
            records[i] = DNSRecord.decodeRecord(inputStream, message);
        }
        return records;
    }

    // 读取域名，处理可能的压缩
    public String[] readDomainName(InputStream in) throws IOException {
        // 确保输入流是ByteArrayInputStream
        if (!(in instanceof ByteArrayInputStream)) {
            throw new IllegalArgumentException("InputStream must be ByteArrayInputStream.");
        }
        return readDomainName((ByteArrayInputStream) in, new HashMap<>());
    }

    // 从特定偏移量开始读取域名
    public String[] readDomainName(int offset) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(this.messageContent, offset, this.messageContent.length - offset);
        return readDomainName(in, new HashMap<>());
    }

    // 实际上读取并处理域名，包括处理压缩
    private String[] readDomainName(ByteArrayInputStream in, Map<Integer, String[]> pointerMap) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        boolean jumped = false;
        int nextByte = in.read();
        // 循环直到遇到域名结束标志（0）
        while (nextByte != 0) {
            // 处理压缩指针
            if ((nextByte & 0xC0) == 0xC0) {
                if (!jumped) jumped = true;
                int secondByte = in.read();
                int pointerOffset = ((nextByte & 0x3F) << 8) + secondByte;
                String[] domainParts = this.readDomainName(pointerOffset);
                for (String part : domainParts) {
                    byteStream.write(part.getBytes());
                    byteStream.write('.');
                }
                break;
            } else {
                byteStream.writeBytes(readLabel(in, nextByte));
                nextByte = in.read();
            }
        }
        if (byteStream.size() > 0) byteStream.write(0);
        String domainName = new String(byteStream.toByteArray(), 0, byteStream.size() - 1);
        return domainName.split("\\.");
    }

    // 读取一个标签（域名的一部分）
    private byte[] readLabel(ByteArrayInputStream in, int length) throws IOException {
        byte[] label = new byte[length + 1]; // 包括点号
        in.read(label, 0, length);
        label[length] = '.';
        return label;
    }

    // 构建响应消息
    public static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers) {
        DNSMessage response = new DNSMessage();
        // 根据请求和提供的答案配置响应
        return response;
    }

    // 将DNSMessage转换为字节序列
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        // 写入消息头和记录
        header.writeBytes(byteStream);
        Map<String, Integer> compressionMap = new HashMap<>();
        for (DNSQuestion question : questions) {
            question.writeBytes(byteStream, (HashMap<String, Integer>) compressionMap);
        }
        writeRecords(byteStream, answers, compressionMap);
        writeRecords(byteStream, authorityRecords, compressionMap);
        writeRecords(byteStream, additionalRecords, compressionMap);
        return byteStream.toByteArray();
    }

    // 写入记录到输出流，支持域名压缩
    private void writeRecords(ByteArrayOutputStream out, DNSRecord[] records, Map<String, Integer> compressionMap) throws IOException {
        for (DNSRecord record : records) {
            record.writeBytes(out, (HashMap<String, Integer>) compressionMap);
        }
    }

    // 写入域名，处理域名压缩
    public static void writeDomainName(ByteArrayOutputStream out, Map<String, Integer> domainLocations, String[] domainParts) throws IOException {
        for (int i = 0; i < domainParts.length; i++) {
            String part = domainParts[i];
            // 检查是否可以使用压缩
            String fullName = String.join(".", java.util.Arrays.copyOfRange(domainParts, i, domainParts.length));
            if (domainLocations.containsKey(fullName)) {
                int pointer = domainLocations.get(fullName);
                out.write((pointer >> 8) | 0xC0);
                out.write(pointer & 0xFF);
                return;
            } else {
                domainLocations.put(fullName, out.size());
                out.write(part.length());
                out.write(part.getBytes());
            }
        }
        out.write(0); // 域名结束标志
    }
}
