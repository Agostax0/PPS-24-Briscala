# Conclusion
All the initially defined functional requirements were fully implemented, demonstrating the robustness of the planning and execution phases. 
In addition, we managed to go beyond the original scope by integrating supplementary features, such as the option to play with bots.

The overall development process proved to be smooth and efficient. 
Furthermore, the early and detailed requirements analysis provided a solid foundation, minimizing ambiguities and technical debt during implementation. 
As a result, the team encountered relatively few obstacles and was able to deliver the project within the expected timeline.

## Future improvements
While the project is already functional and meets all initial expectations, there are various areas where the codebase could be refined and extended to improve maintainability, scalability, and alignment with modern and more functional programming paradigms.

A class that would benefit from refactoring is `EngineController`. Its current design is not strongly aligned with a state-oriented approach, which makes the logic more rigid and less adaptable to change. Reworking the class to follow a more state-driven architecture would simplify the addition of new game behaviors or modes. This shift would also enhance the overall readability of the code and make unit testing more straightforward and effective.

Finally, more advanced functional programming patterns could have been employed in place of the older, Java-like approaches currently in use. 
Additionally, the bot could be extended by introducing a new strategy that leverages logic programming, using Prolog, to compute the next card to play. 

| [Previous Chapter](../8-retrospective/index.md) | [Index](../index.md) |
