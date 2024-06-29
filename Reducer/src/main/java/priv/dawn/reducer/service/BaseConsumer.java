package priv.dawn.reducer.service;

public abstract class BaseConsumer<T> {

    // 幂等消费逻辑, 异常直接抛出表示重复消费
    protected void doConsumer(T record) {
        if (createOrder(record))
            consumeMessage(record);
    }

    // 创建唯一业务标识订单
    protected abstract boolean createOrder(T record);

    // 消费信息
    protected abstract void consumeMessage(T record);


}
