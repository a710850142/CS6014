import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class DNSHeader {
    // 定义DNS头部的各个字段
    private final int transactionID; // 事务ID，用于匹配请求和响应
    private final int flags; // 标志位，用于控制和指示查询/响应的状态
    private final int questions; // 问题数，指示查询中的问题数量
    private final int answerRRs; // 应答资源记录数，指示响应中包含的资源记录数量
    private final int authorityRRs; // 授权资源记录数，指示授权部分的资源记录数量
    private final int additionalRRs; // 附加资源记录数，指示附加部分的资源记录数量

    // 构造函数私有化，确保DNS头部的不变性通过静态工厂方法创建
    private DNSHeader(int transactionID, int flags, int questions, int answerRRs, int authorityRRs, int additionalRRs) {
        this.transactionID = transactionID;
        this.flags = flags;
        this.questions = questions;
        this.answerRRs = answerRRs;
        this.authorityRRs = authorityRRs;
        this.additionalRRs = additionalRRs;
    }

    // 从输入流中解码DNS头部的静态方法
    public static DNSHeader decodeHeader(InputStream in) throws IOException {
        byte[] data = new byte[12]; // DNS头部固定长度为12字节
        if (in.read(data) != data.length) {
            throw new IOException("Failed to read the complete DNS header");
        }
        ByteBuffer buffer = ByteBuffer.wrap(data); // 使用ByteBuffer简化二进制数据操作

        // 读取各个字段，使用&0xFFFF确保值为无符号整数
        return new DNSHeader(
                buffer.getShort() & 0xFFFF,
                buffer.getShort() & 0xFFFF,
                buffer.getShort() & 0xFFFF,
                buffer.getShort() & 0xFFFF,
                buffer.getShort() & 0xFFFF,
                buffer.getShort() & 0xFFFF
        );
    }

    // 根据请求头部信息创建响应消息的DNS头部
    public static DNSHeader buildHeaderForResponse(DNSHeader requestHeader, int answerCount) {
        int responseFlags = requestHeader.flags | 0x8000; // 设置响应标志，QR位为1
        return new DNSHeader(
                requestHeader.transactionID,
                responseFlags,
                requestHeader.questions,
                answerCount,
                0, // 在简单实现中不使用authorityRRs和additionalRRs
                0
        );
    }

    // 方法将DNS头部写入输出流
    public void writeBytes(OutputStream out) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(12); // 分配12字节缓冲区
        // 将各个字段写入缓冲区
        buffer.putShort((short) this.transactionID)
                .putShort((short) this.flags)
                .putShort((short) this.questions)
                .putShort((short) this.answerRRs)
                .putShort((short) this.authorityRRs)
                .putShort((short) this.additionalRRs);

        out.write(buffer.array()); // 将缓冲区的内容写入输出流
    }

    @Override
    public String toString() {
        // 重写toString方法，便于调试和日志记录
        return String.format("DNSHeader{transactionID=%d, flags=%d, questions=%d, answerRRs=%d, authorityRRs=%d, additionalRRs=%d}",
                this.transactionID, this.flags, this.questions, this.answerRRs, this.authorityRRs, this.additionalRRs);
    }

    // Getter方法，提供对私有字段的访问
    public int getTransactionID() { return this.transactionID; }
    public int getFlags() { return this.flags; }
    public int getQuestions() { return this.questions; }
    public int getAnswerRRs() { return this.answerRRs; }
    public int getAuthorityRRs() { return this.authorityRRs; }
    public int getAdditionalRRs() { return this.additionalRRs; }
}
