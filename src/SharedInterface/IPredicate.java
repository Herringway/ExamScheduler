package SharedInterface;

/**
 * Useful for filters and other unary operations.
 *
 * @author John Maguire
 *
 * @param <T>
 */
public interface IPredicate<T> {
    public boolean apply(T type);
}
