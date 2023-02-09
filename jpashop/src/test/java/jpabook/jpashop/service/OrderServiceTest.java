package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook();

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 합니당");
        assertEquals(10000*orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량입니당");
        assertEquals(10, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 합니당");
    }

    private Item createBook() {
        return createBook("발성 기본편", 10000, 12);
    }

    private Item createBook(String title, int price, int quantity) {
        Item book = new Book();
        book.setName(title);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("홍길동");
        member.setAddress(new Address("서울", "한남", "506-506"));
        em.persist(member);
        return member;
    }

    @Test
    public void 주문취소() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 상품주문_재고수량부족() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook("발성 기본편", 10000, 10);
        int orderCount = 11;
        //when

        //then
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        });
    }
}