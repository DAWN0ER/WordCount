package priv.dawn.workers.service.impl;

@Deprecated
//@DubboService
//@Slf4j
//@Service
public class WorkerServiceImpl {
/*

    @Qualifier(value = "WorkerReadThreadPool")
    @Resource
    private ThreadPoolTaskExecutor readThreadPool;

    @Autowired
    private ChunkReadMapper chunkReadMapper;
    @Autowired
    private ProgressDao progressDao;
    @Autowired
    private WordCountMapper wordCountMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "word_count";

    // DONE: 2024/5/4 这边线程不够用了之后会抛出拒绝异常, 在这边捕获返回值就是已经进入线程池的chunk的id, 表示完成个数
//    @Override
    public int loadFile(int fileUID, int chunkBegin, int chunkNum) {
        log.info("load " + chunkNum + " chunks of file: " + fileUID + " begin form chunk " + chunkBegin);
        int chunkEnd = chunkBegin + chunkNum - 1;
        List<ChunkDTO> chunks = chunkReadMapper.getChunks(fileUID, chunkBegin, chunkEnd);
        if (chunks.size() != chunkNum) return -1;
        //上面都是为了模拟分布式分块存储系统的交互, 因为这个框架里面没有这样一个实际的系统, 所以写的很潦草

        int finished = 0;
        for (ChunkDTO chunk : chunks) {
            try {
                readThreadPool.execute(new processChunk(fileUID, chunk));
                finished = finished + 1;
            } catch (RejectedExecutionException e) {
                log.error("When file:" + fileUID + " chunk " + chunk.getChunkId() + " was executed, the ThreadPool rejected");
                break;
            }
        }
        return finished; // 真要出问题了反正查日志去
    }

//    @Override
    public float getProgress(int fileUID) {
        return progressDao.getProgress(fileUID);
    }

//    @Override
    public List<String> getWords(int fileUID) {
        return wordCountMapper.getTop100WordCount(fileUID);
    }

//    @Override
    public boolean createOrder(int fileUID, int chunkNum) {
        return progressDao.createProgress(fileUID, chunkNum);
    }


    // DONE: 2024/5/4  Runner 处理 chunk 的, 整合了 Kafka , 需要解决序列化问题和分区的mapper问题
    // 使用内部类就可以直接使用 kafkaTemplate 更加方便
    @AllArgsConstructor
    private class processChunk implements Runnable {

        private int fileUID;
        private ChunkDTO chunk;

        @Override
        public void run() {

            int partitionNum = kafkaTemplate.partitionsFor(TOPIC).size();

            // 构造消息并发送
            ArrayList<CustomMessage> messages = CustomMassageUtil.generateFromChunk(chunk, partitionNum);
            for (int partition = 0; partition < partitionNum; partition++) {
                CustomMessage msg = messages.get(partition);
                String key = fileUID + "-" + chunk.getChunkId() + "-" + partition + "-" + partitionNum; // 根据 key 可以精确定位幂等
                kafkaTemplate.send(TOPIC, partition, key, msg.toJsonStr()).addCallback(
                        success -> {},
                        failure -> log.error("fail to send massage: " + failure)
                );
            }
        }
    }

// */
}
