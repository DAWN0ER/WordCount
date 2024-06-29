package priv.dawn.reducer.service;

public abstract class BaseConsumer<T> {

    protected void doConsumer(T record){
        // 幂等消费逻辑
    }

    // 判断订单是否已经存在
    protected abstract boolean isOrderExist(T record);
    // 创建订单
    protected abstract boolean createOrder(T record);
    // 消费信息
    protected abstract boolean consumeMessage(T record);
    // 消费失败处理
    protected abstract void doWhenFail(T record);


}
