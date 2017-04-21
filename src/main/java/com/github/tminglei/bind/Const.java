package com.github.tminglei.bind;

/**
 * Created by minglei on 4/21/17.
 */
public interface Const {
  ///--- constraint
  String CONSTRAINT_REQUIRED = "required";
  String CONSTRAINT_LENGTH = "length";
  String CONSTRAINT_MAX_LENGTH = "maxLength";
  String CONSTRAINT_MIN_LENGTH = "minLength";
  String CONSTRAINT_ONE_OF = "oneOf";
  String CONSTRAINT_EMAIL = "email";
  String CONSTRAINT_PATTERN = "pattern";
  String CONSTRAINT_PATTERN_NOT = "patternNot";
  String CONSTRAINT_INDEX_IN_KEYS = "indexInKeys";

  String EX_CONSTRAINT_MIN = "min";
  String EX_CONSTRAINT_MAX = "max";

  ///--- mapping
  String MAPPING_STRING = "string";
  String MAPPING_BOOLEAN = "boolean";
  String MAPPING_INT = "int";
  String MAPPING_LONG = "long";
  String MAPPING_DOUBLE = "double";
  String MAPPING_FLOAT = "float";
  String MAPPING_BIG_DECIMAL = "bigDecimal";
  String MAPPING_BIG_INTEGER = "bitInteger";
  String MAPPING_UUID = "uuid";
  String MAPPING_DATE = "date";
  String MAPPING_TIME = "time";
  String MAPPING_DATE_TIME = "datetime";

  ///--- processor
  String PRE_PROCESSOR_TRIM = "trim";
  String PRE_PROCESSOR_OMIT = "omit";
  String PRE_PROCESSOR_OMIT_LEFT = "omitLeft";
  String PRE_PROCESSOR_OMIT_RIGHT = "omitRight";
  String PRE_PROCESSOR_OMIT_REDUNDANT = "omitRedundant";
  String PRE_PROCESSOR_OMIT_MATCHED = "omitMatched";
  String PRE_PROCESSOR_REPLACE_MATCHED = "replaceMatched";
  String PRE_PROCESSOR_EXPAND_JSON = "expandJson";
  String PRE_PROCESSOR_EXPAND_JSON_KEYS = "expandJsonKeys";
  String PRE_PROCESSOR_EXPAND_LIST_KEYS = "expandListKeys";
  String PRE_PROCESSOR_CHANGE_PREFIX = "changePrefix";
}
