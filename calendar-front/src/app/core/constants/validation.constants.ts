export const ValidationConstants = {
  ALPHANUMERIC_PATTERN: /^[A-Za-z0-9\s\-@]+$/,
  ALPHANUMERIC_PATTERN_STRING: '^[A-Za-z0-9\\s\\-@]+$',

  MESSAGES: {
    REQUIRED: 'This field is required',
    INVALID_PATTERN: 'Only alphanumeric characters, spaces, hyphens, and @ symbols allowed',
    FIELD_REQUIRED: (fieldName: string) => `${fieldName} is required`,
    START_TIME_BEFORE_END_TIME: 'Start time must be before end time',
  }
} as const;


