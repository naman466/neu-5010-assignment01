# Design Introspection

Your own reflection on the design decisions you made this week. Written in your own words — this is
distinct from the LLM's assessment of your code, and distinct from your code-walk video.

Aim for a page. Cite your actual code: name the class and method you are talking about.

## 1. What you built

In two or three sentences: which types you wrote this week, and what each one is responsible for.

`` This week I wrote Animal and AgeMonths. AgeMonths is responsible for handling and validating the Age for any given animal. Animal is the main class which gives us information about the Animal, their name, intakeDate etc without exposing any of the data where it can be modified. ``
## 2. Design decisions

Pick the two or three decisions you actually had to think about, and for each one:

- **What was the choice?** What were the alternatives you considered?
- **What did you pick, and why?** What would have gone wrong with the other option?
- **What did it cost?** Every real design decision costs something.

1. Handling the nested conditions in `AgeMonths.toString()`
- The choice was how to handle the nested conditions in the requirements. The initial design I came up with was a web of if-else conditions which was not very readable and could cause issues down the line. I then zeroed in on a  simpler version of the conditions.
- I picked the simpler or the more readable version because that would be more maintainable if someone else looked at my code in the future and had to change something. 
- In terms of the cost, it cost me time. Instead of moving on to the next part, I had to spend time thinking of a better approach, writing it, and then validating if it worked as expected.
2. Validating the `Animal` constructor
- Initially, I assigned the values after validating them individually. So instead of validating all then assigning all, what I wrote was validate individual attributes and assign them immediately.
- I chose to validate all then assign all attributes as that aligned more with the specifications, and also ensures that the object is created only after checking that all the inputs are valid. 
- This delays object creation to after all the validation has happened, so the constructor cannot finalize each field as it goes. However, this ensures that an invalid object isn't created and a validation boundary exists.
3. Delegating age formatting to `AgeMonths`
- When writing the `Animal.toString()` method I could have either used the logic already established in `AgeMonths` to format the age, or rewrite it again in the `Animal.toString()` method. 
- I chose to use the already existing implementation to make the method simpler. It also follows the princple that we should reuse code as much as possible.
- It costs us flexibility in terms of how we want to display the age in the `Animal` class. In the future, if we decide that we want the `Animal.toString()` method to show the age as 1.XX years instead of the string, we would have to re-implement the `AgeMonths.toString()` method or implement new logic in the `Animal.toString()` method if we want to keep the AgeMonths representation untouched.

Good candidates: where you put a piece of behaviour and why it belongs there rather than somewhere
else; how you represented something so that an invalid version could not be built; where you chose to
delegate to existing code rather than re-deriving an answer.

## 3. Invariants

What does your code guarantee about itself, and where is each guarantee enforced?

For each type that validates its input: what must always be true of an instance once it exists, and
which line makes that true? If a guarantee is enforced in more than one place, say why — and whether
that is deliberate or duplication.

`AgeMonths` guarantees that an age can never be negative or greater than `MAX_MONTHS`. This is enforced in the `AgeMonths.of()` method, where the input is checked before a new `AgeMonths` object is created.

`Animal` guarantees that the name is not null or blank, and that the `species`, `age`, and `intakeDate` are not null. These checks are enforced in the `Animal` constructor before the fields are assigned. The constructor also guarantees that leading and trailing whitespace is removed from the name using `name.trim()`.

The fields in `Animal` are also `private final`, and there are no setters, so the stored values cannot be changed through the public interface after the object has been created. The same idea is used by `AgeMonths`, where `months` is a private final field and the value is only set when the object is constructed.


## 4. Testing

- Which cases did you add beyond the provided tests, and what made you think of them?
- Which test was hardest to write, and what did writing it teach you about your own design?
- What is still untested, and how would you test it if you had another hour?

I added tests beyond the provided tests mainly around exception message content and boundary values. In `AgeMonthsAdditionalTest`, I tested `AgeMonths.MAX_MONTHS - 1` to make sure the value immediately below the maximum is handled correctly, including its years, remaining months, and string representation. I also tested that the exception message for a negative age actually contains the rejected value.

In AnimalAdditionalTest, I tested the exception messages for a null name and a null species. The provided tests already checked that these inputs throw an exception, but they did not check whether the message actually identifies the problem. (Inspired from the very first workshop, where the test checked if we replaced the placeholder with our name.)

The tests themselves were not difficult to write. The harder part was going through the provided tests and deciding which possible tests were relevant and which would just duplicate behavior that was already checked. Since the provided suite already covers most of the implementation, I had to filter out tests that would only increase the coverage number without testing anything new.

I would write tests around the whitespace handling for name intake in the `Animal` constructor.
## 5. What you would change

Given another day, what would you do differently — and what stopped you this week? Be specific;
"write more tests" is not an answer.

I would spend more time planning the implementation of `AgeMonths.toString()` before writing the first version. I would prefer to have my methodology for the section locked in before I actually start writing, rather than having to change midway.

## 6. What you found hard

The honest one. What took the longest, what did you get wrong first, and what finally made it click?
This is not marked on whether you struggled — everyone does — but on whether you can say clearly
where and why.

The part that took the longest was `AgeMonths.toString()`. The different combinations of years and months, along with singular and plural forms, made it easy to create a complicated set of conditions. I initially got this wrong by making the logic more complicated than it needed to be.

