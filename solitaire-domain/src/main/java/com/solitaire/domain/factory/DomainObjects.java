package com.solitaire.domain.factory;

import com.solitaire.domain.Board;
import com.solitaire.domain.rules.Rules;

public record DomainObjects(Board board, Rules rules) {}
