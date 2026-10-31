package validation;

/**
 * Validates an entity of type T.
 *
 * @param <T> the type of entity being validated
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Validates the given entity.
     *
     * @param entity the entity to validate
     * @throws exceptions.ValidationException if validation fails
     */
    void validate(T entity);
    /**
     * Combines the validator with another validator.
     *
     * @param other the validator executed after this one
     * @return a validator that executes both validation in order
     * @throws IllegalArgumentException if other is null
     */
    default Validator<T> andThen(Validator<T> other){
        if(other==null){
            throw new IllegalArgumentException("Validator cannot be null.");
        }
        return entity->{
            this.validate(entity);
            other.validate(entity);
        };
    }
}
