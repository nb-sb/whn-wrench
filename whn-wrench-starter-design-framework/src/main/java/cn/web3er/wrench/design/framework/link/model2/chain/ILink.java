package cn.web3er.wrench.design.framework.link.model2.chain;
/**
* @author: Wanghaonan @戏人看戏
* @description: 链接口，定义链表基本内容
* @create: 2025/5/22 15:05
*/
public interface ILink<E> {

    boolean add(E e);

    boolean addFirst(E e);

    boolean addLast(E e);

    boolean remove(Object o);

    E get(int index);

    void printLinkList();
}
