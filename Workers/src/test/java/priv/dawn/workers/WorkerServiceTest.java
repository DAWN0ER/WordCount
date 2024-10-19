package priv.dawn.workers;

@Deprecated
public class WorkerServiceTest extends WorkersApplicationTests {
//
//    @Autowired
//    WorkerService service;
//
//    @Autowired
//    ProgressDao manager;
//
//    @Autowired
//    KafkaTemplate<String, String> kafka;
//
//    @Autowired
//    WordCountMapper wordCountMapper;
//
//
//    @Test
//    public void simpleTest() throws InterruptedException {
//        int file = 1222161892;
//        int chunkNum = 6;
//
//        manager.createProgress(file, chunkNum); // 创建进程信息(订单信息)
//
//        int res = service.loadFile(file, 1, chunkNum);
//        log.info("finished " + res);
//        Thread.sleep(5 * 1000); // 等15s看情况
//        log.info("Progress: " + service.getProgress(file));
//        log.info("exit");
//    }
//
//    @Test
//    public void kafkaTest() {
//        List<PartitionInfo> list = kafka.partitionsFor("word_count");
//        for (PartitionInfo info : list) {
//            log.info(info.toString());
//        }
//    }
//
//    @Test
//    public void wordCountClient() {
//        // 本地找到这个文件的信息
//        int fileUID = 923965605;
//        int chunkNum = 91;
//
////        if(chunkNum<=0) return;
//        if (!service.createOrder(fileUID, chunkNum)) return;
//
//        // 每个chunk 是 2kb 的数据, 希望每个worker能一次处理2mb-3mb的数据
//        log.info("Order created: " + fileUID);
//        int chunksPreWorker = 10;
//        int workersNum = Math.round(1.f * chunkNum / chunksPreWorker);
//        int begin = 1;
//        while (workersNum-- > 1) {
//            service.loadFile(fileUID, begin, chunksPreWorker);
//            begin += chunksPreWorker;
//        }
//        service.loadFile(fileUID, begin, chunkNum - begin + 1);
//
//        long millis = System.currentTimeMillis();
//        System.out.println("Progress...");
//        while (service.getProgress(fileUID) < 100) webSocket();
//        long spend = System.currentTimeMillis() - millis;
//        log.info("Time spend " + spend);
//    }
//
//    @Test
//    public void mapperTest() {
//        int uid = 923965605;
//        List<String> wordCount = wordCountMapper.getTop100WordCount(uid);
//        List<String> words = wordCountMapper.getTop100Words(uid);
//        log.info(words.toString());
//        log.info(wordCount.toString());
//    }
//
//    private void webSocket() {
//    }// 模拟 websocket 进度条
}
